package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.XyoException
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import kotlin.collections.ArrayList

/**
 * A zig-zag bound witness protocol (the primary, and most common).
 *
 * A -
 *      -                sends fetter (a)
 *          - B
 *      _                sends witness and fetter (b, b)
 *  A -
 *        -              send witness (a)
 *           - B
 *
 *
 * @property signers the signers to sign the bound witness with.
 * @property signedPayload the signed payload to pur in the bound witness fetter.
 * @property unsignedPayload the un-signed payload to pur in the bound witness witness.
 */
open class XyoZigZagBoundWitness(private val signers : Array<XyoSigner>,
                                 private val signedPayload : Array<XyoBuff>,
                                 private val unsignedPayload: Array<XyoBuff>) : XyoBoundWitness() {

    /**
     * A ledger to add and remove bound witness fetters and witness while constructing.
     */
    private val dynamicLeader = ArrayList<XyoBuff>()

    /**
     * The offset where to read from the buffer, this is set to 0 by default (beginning of the ByteArray).
     */
    override val allowedOffset: Int = 0

    /**
     * The bytes of the bound witness.
     */
    override var item: ByteArray = byteArrayOf()
        get() {
            return XyoIterableObject.createUntypedIterableObject(XyoSchemas.BW, dynamicLeader.toTypedArray()).bytesCopy
        }

    /**
     * The state carried, if the party creating this bound witness has sent the fetter
     */
    private var hasSentFetter = false

    /**
     * Adds data to the bound witness and returns whats the party should send back.
     *
     * @param transfer The data from the other party.
     * @param endPoint If not already turned around, decide if what to send sign and send back.
     * @return A XyoBoundWitnessTransfer to send to the other party.
     */
    fun incomingData (transfer : XyoIterableObject?, endPoint : Boolean) : Deferred<XyoIterableObject> = GlobalScope.async {
        if (transfer != null) {
            addTransfer(transfer).await()
        }

        if (!hasSentFetter) {
            dynamicLeader.add(createFetter(signedPayload, makeSelfKeySet()))
            hasSentFetter = true
        }

        if (numberOfWitnesses != numberOfFetters) {
            return@async getReturnFromIncoming(getNumberOfWitnessesFromTransfer(transfer), endPoint, unsignedPayload).await()
        }

        return@async encodeTransfer(arrayOf())
    }

    /**
     * Gets the number of witnesses in a transfer object. This is used for calculating what to send next.
     *
     * @param transfer The transfer object to count the signatures
     * @return The number of signatures/witnesses in the transfer.
     */
    private fun getNumberOfWitnessesFromTransfer (transfer: XyoIterableObject?) : Int {
        if (transfer == null) {
            return 0
        }

        return transfer[XyoSchemas.WITNESS.id].size
    }

    /**
     * Gets the incommoding data, and figures out what to send back according to a Zig-Zag Bound Witness.
     *
     * @param signatureReceivedSize The number of witnesses in the bound witness.
     * @param endPoint If not already turned aground, if wanting to turn the bound witness aground
     * @param payload The payload to add to the bound witness.
     * @return The response to send back to the other party connected to.
     */
    private fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean, payload : Array<XyoBuff>) : Deferred<XyoIterableObject> = GlobalScope.async {
        if (numberOfWitnesses == 0 && !endPoint) {
            return@async encodeTransfer(dynamicLeader.toTypedArray())
        }

        return@async passAndSign(signatureReceivedSize, payload).await()
    }

    /**
     * Gets the current stated of the bound witness, signs it, and returns the proper response to send back to the
     * other parties.
     *
     * @param signatureReceivedSize The number of witnesses in the bound witness.
     * @param payload The payload to add to the bound witness.
     * @return The response to send to the other parties.
     */
    private fun passAndSign (signatureReceivedSize: Int, payload: Array<XyoBuff>) : Deferred<XyoIterableObject> = GlobalScope.async {
        val toSend = ArrayList<XyoBuff>()

        signForSelf(payload).await()

        val publicKeyIt = this@XyoZigZagBoundWitness[XyoSchemas.FETTER.id]
        for (i in signatureReceivedSize + 1 until publicKeyIt.size ) {
            toSend.add(publicKeyIt[i])
        }

        val signatureIt = this@XyoZigZagBoundWitness[XyoSchemas.WITNESS.id]
        toSend.add(signatureIt[signatureIt.size - 1])

        return@async encodeTransfer(toSend.toTypedArray())
    }

    /**
     * Encodes an array of things to send to the other party into a proper transfer object. This is used to
     * distinguish when to use a fetter set, witness set, or Bound Witness Fragment.
     *
     * @param itemsToTransfer The items to encode into a transfer object.
     * @return An XyoIterableObject containing the items.
     */
    private fun encodeTransfer (itemsToTransfer : Array<XyoBuff>) : XyoIterableObject {
        val fetters = ArrayList<XyoBuff>()
        val witnesses =  ArrayList<XyoBuff>()

        for (item in itemsToTransfer) {
            when (item.schema.id) {
                XyoSchemas.FETTER.id -> {
                    fetters.add(item)
                }
                XyoSchemas.WITNESS.id ->  {
                    witnesses.add(item)
                }
            }
        }

        if (fetters.size == 0 && witnesses.size != 0) {
            return XyoIterableObject.createTypedIterableObject(XyoSchemas.WITNESS_SET, witnesses.toTypedArray())
        } else if(numberOfFetters != 0 && numberOfWitnesses == 0) {
            return XyoIterableObject.createTypedIterableObject(XyoSchemas.FETTER_SET, fetters.toTypedArray())
        }

        return  XyoIterableObject.createUntypedIterableObject(XyoSchemas.BW_FRAGMENT, itemsToTransfer)
    }

    /**
     * Adds the current transfer to the Bound Witness ledger.
     *
     * @param transfer The transfer to add
     */
    private fun addTransfer (transfer : XyoIterableObject) : Deferred<Unit> = GlobalScope.async {
        // drill down on transfer to make sure its valid
        transfer.toString()

        for (item in transfer.iterator) {
            if (item.schema.id != XyoSchemas.FETTER.id && item.schema.id != XyoSchemas.WITNESS.id) {
                throw XyoBoundWitnessCreationException("Item must be fetter or witness")
            }
            dynamicLeader.add(item)
        }
    }

    /**
     * Makes the key set for thr current party in this bound witness.
     *
     * @return The key set of the acting party.
     */
    private fun makeSelfKeySet() : XyoBuff {
        val publicKeys = ArrayList<XyoBuff>()
        for (signer in signers) {
            publicKeys.add(signer.publicKey)
        }
        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.KEY_SET, publicKeys.toTypedArray())
    }

    /**
     * Signs the current state of the bound witness.
     *
     * @param payload The payload to add to the witness.
     * @return The newly created witness.
     */
    private fun signBoundWitness (payload: Array<XyoBuff>) : Deferred<XyoBuff> = GlobalScope.async {
        return@async createWitness(payload, XyoIterableObject.createUntypedIterableObject(XyoSchemas.SIGNATURE_SET, Array(signers.size) { i ->
            signCurrent(signers[i]).await()
        }))
    }

    /**
     * Signs the current bound witness and ads it to the Bound Witness ledger.
     *
     * @param payload The payload to sign with (add into the witness, unsigned payload)
     */
    private fun signForSelf (payload: Array<XyoBuff>) = GlobalScope.async {
        val signatureSet = signBoundWitness(payload).await()
        dynamicLeader.add(signatureSet)
    }
}