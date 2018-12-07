package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import kotlin.collections.ArrayList

/**
 * A zig-zag bound witness protocol.
 *
 * @param signers the signers to sign the bound witness with.
 * @param payload the payload to pur in the bound witness.
 */

open class XyoZigZagBoundWitness(private val signers : Array<XyoSigner>,
                                 private val signedPayload : Array<XyoBuff>,
                                 private val unsignedPayload: Array<XyoBuff>) : XyoBoundWitness() {

    private val dynamicLeader = ArrayList<XyoBuff>()

    override val allowedOffset: Int
        get() = 0

    override var item: ByteArray
        get() {
            return XyoIterableObject.createUntypedIterableObject(XyoSchemas.BW, dynamicLeader.toTypedArray()).bytesCopy
        }
        set(value) {}

    private var hasSentKeysAndPayload = false

    private val numberOfFetters : Int
        get() {
            return this[XyoSchemas.FETTER.id].size
        }

    private val numberOfWitnesses : Int
        get() {
            return this[XyoSchemas.WITNESS.id].size
        }

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

        if (!hasSentKeysAndPayload) {
            dynamicLeader.add(createFetter(signedPayload, makeSelfKeySet()))
            hasSentKeysAndPayload = true
        }

        if (numberOfWitnesses != numberOfFetters) {
            return@async getReturnFromIncoming(getNumberOfSignaturesFromTransfer(transfer), endPoint, unsignedPayload).await()
        }

        return@async encodeTransfer(arrayOf())
    }

    private fun getNumberOfSignaturesFromTransfer (transfer: XyoIterableObject?) : Int {
        if (transfer == null) {
            return 0
        }

        return transfer[XyoSchemas.WITNESS.id].size
    }

    private fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean, payload : Array<XyoBuff>) : Deferred<XyoIterableObject> = GlobalScope.async {

        if (numberOfWitnesses == 0 && !endPoint) {
            return@async encodeTransfer(dynamicLeader.toTypedArray())
        }

        return@async passAndSign(signatureReceivedSize, payload).await()
    }

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

    private fun addTransfer (transfer : XyoIterableObject) : Deferred<Unit> = GlobalScope.async {
        for (item in transfer.iterator) {
            dynamicLeader.add(item)
        }
    }

    private fun makeSelfKeySet() : XyoBuff {
        val publicKeys = ArrayList<XyoBuff>()
        for (signer in signers) {
            publicKeys.add(signer.publicKey)
        }
        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.KEY_SET, publicKeys.toTypedArray())
    }

    private fun signBoundWitness (payload: Array<XyoBuff>) = GlobalScope.async {
        return@async createWitness(payload, XyoIterableObject.createUntypedIterableObject(XyoSchemas.SIGNATURE_SET, Array(signers.size) { i ->
            signCurrent(signers[i]).await()
        }))
    }

    private fun signForSelf (payload: Array<XyoBuff>) = GlobalScope.async {
        val signatureSet = signBoundWitness(payload).await()
        dynamicLeader.add(signatureSet)
    }
}