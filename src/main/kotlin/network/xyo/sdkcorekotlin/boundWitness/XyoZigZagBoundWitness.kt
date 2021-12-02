package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
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
 * @property signedPayload the signed payload to put in the bound witness fetter.
 * @property unsignedPayload the un-signed payload to put in the bound witness witness.
 */
open class XyoZigZagBoundWitness(private val signers : Array<XyoSigner>,
                                 private val signedPayload : Array<XyoObjectStructure>,
                                 private val unsignedPayload: Array<XyoObjectStructure>) : XyoBoundWitness() {

    /**
     * A ledger to add and remove bound witness fetters and witness while constructing.
     */
    private val dynamicLeader = ArrayList<XyoObjectStructure>()

    override var item: ByteArray = byteArrayOf()
        get() =  XyoIterableStructure.createUntypedIterableObject(XyoSchemas.BW, dynamicLeader.toTypedArray()).bytesCopy

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
    suspend fun incomingData (transfer : XyoIterableStructure?, endPoint : Boolean) : XyoIterableStructure? {
        if (transfer != null) {
            addTransfer(transfer)
        }

        if (!hasSentFetter) {
            dynamicLeader.add(createFetter(signedPayload, makeSelfKeySet()))
            hasSentFetter = true
        }

        if (numberOfWitnesses != numberOfFetters) {
            return getReturnFromIncoming(getNumberOfWitnessesFromTransfer(transfer), endPoint, unsignedPayload)
        }

        return encodeTransfer(arrayOf())
    }

    /**
     * Gets the number of witnesses in a transfer object. This is used for calculating what to send next.
     *
     * @param transfer The transfer object to count the signatures
     * @return The number of signatures/witnesses in the transfer.
     */
    private fun getNumberOfWitnessesFromTransfer (transfer: XyoIterableStructure?) : Int {
        if (transfer == null) {
            return 0
        }

        return transfer[XyoSchemas.WITNESS.id].size
    }

    /**
     * Gets the incoming data, and figures out what to send back according to a Zig-Zag Bound Witness.
     *
     * @param signatureReceivedSize The number of witnesses in the bound witness.
     * @param endPoint If not already turned aground, if wanting to turn the bound witness aground
     * @param payload The payload to add to the bound witness.
     * @return The response to send back to the other party connected to.
     */
    private suspend fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean, payload : Array<XyoObjectStructure>) : XyoIterableStructure {
        if (numberOfWitnesses == 0 && !endPoint) {
            return encodeTransfer(dynamicLeader.toTypedArray())
        }

        return passAndSign(signatureReceivedSize, payload)
    }

    /**
     * Gets the current stated of the bound witness, signs it, and returns the proper response to send back to the
     * other parties.
     *
     * @param signatureReceivedSize The number of witnesses in the bound witness.
     * @param payload The payload to add to the bound witness.
     * @return The response to send to the other parties.
     */
    private suspend fun passAndSign (signatureReceivedSize: Int, payload: Array<XyoObjectStructure>) : XyoIterableStructure {
        val toSend = ArrayList<XyoObjectStructure>()

        signForSelf(payload)

        val publicKeyIt = this@XyoZigZagBoundWitness[XyoSchemas.FETTER.id]
        for (i in signatureReceivedSize + 1 until publicKeyIt.size ) {
            toSend.add(publicKeyIt[i])
        }

        val signatureIt = this@XyoZigZagBoundWitness[XyoSchemas.WITNESS.id]
        toSend.add(signatureIt[signatureIt.size - 1])

        return encodeTransfer(toSend.toTypedArray())
    }

    /**
     * Encodes an array of things to send to the other party into a proper transfer object. This is used to
     * distinguish when to use a fetter set, witness set, or Bound Witness Fragment.
     *
     * @param itemsToTransfer The items to encode into a transfer object.
     * @return An XyoIterableObject containing the items.
     */
    private fun encodeTransfer (itemsToTransfer : Array<XyoObjectStructure>) : XyoIterableStructure {
        val fetters = ArrayList<XyoObjectStructure>()
        val witnesses =  ArrayList<XyoObjectStructure>()

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
            return XyoIterableStructure.createTypedIterableObject(XyoSchemas.WITNESS_SET, witnesses.toTypedArray())
        } else if(numberOfFetters != 0 && numberOfWitnesses == 0) {
            return XyoIterableStructure.createTypedIterableObject(XyoSchemas.FETTER_SET, fetters.toTypedArray())
        }

        return  XyoIterableStructure.createUntypedIterableObject(XyoSchemas.BW_FRAGMENT, itemsToTransfer)
    }

    /**
     * Adds the current transfer to the Bound Witness ledger.
     *
     * @param transfer The transfer to add
     */
    private fun addTransfer (transfer : XyoIterableStructure) {
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
    private fun makeSelfKeySet() : XyoObjectStructure {
        val publicKeys = ArrayList<XyoObjectStructure>()
        for (signer in signers) {
            publicKeys.add(signer.publicKey)
        }
        return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.KEY_SET, publicKeys.toTypedArray())
    }

    /**
     * Signs the current state of the bound witness.
     *
     * @param payload The payload to add to the witness.
     * @return The newly created witness.
     */
    private suspend fun signBoundWitness (payload: Array<XyoObjectStructure>) : XyoObjectStructure {
        return createWitness(payload, XyoIterableStructure.createUntypedIterableObject(XyoSchemas.SIGNATURE_SET, Array(signers.size) { i ->
            signCurrent(signers[i])
        }))
    }

    /**
     * Signs the current bound witness and adds it to the Bound Witness ledger.
     *
     * @param payload The payload to sign with (add into the witness, unsigned payload)
     */
    private suspend fun signForSelf (payload: Array<XyoObjectStructure>) {
        val signatureSet = signBoundWitness(payload)
        dynamicLeader.add(signatureSet)
    }
}