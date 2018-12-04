package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.util.*

/**
 * A zig-zag bound witness protocol.
 *
 * @param signers the signers to sign the bound witness with.
 * @param payload the payload to pur in the bound witness.
 */

open class XyoZigZagBoundWitness(private val signers : Array<XyoSigner>,
                                 private val signedPayload : XyoBuff,
                                 private val unsignedPayload: XyoBuff) : XyoBoundWitness() {

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
            return this[XyoSchemas.WITNESSS.id].size
        }

    private fun createFetter (payload: XyoBuff, publicKeys : XyoBuff) : XyoBuff {
        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.FETTER, arrayOf(payload, publicKeys))
    }

    private fun createWitness (payload: XyoBuff, signatures : XyoBuff) : XyoBuff {
        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.WITNESSS, arrayOf(payload, signatures))
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
            dynamicLeader.add(createFetter(makeSelfKeySet(), signedPayload))
            hasSentKeysAndPayload = true
        }

        if (numberOfWitnesses != numberOfFetters) {
            return@async getReturnFromIncoming(getNumberOfSignaturesFromTransfer(transfer), endPoint, unsignedPayload).await()
        }

        return@async XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
    }

    private fun getNumberOfSignaturesFromTransfer (transfer: XyoIterableObject?) : Int {
        if (transfer == null) {
            return 0
        }

        return transfer[XyoSchemas.WITNESSS.id].size
    }

    private fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean, payload : XyoBuff) : Deferred<XyoIterableObject> = GlobalScope.async {

        if (numberOfWitnesses == 0 && !endPoint) {
            return@async this@XyoZigZagBoundWitness
        }

        return@async passAndSign(signatureReceivedSize, payload).await()
    }

    private fun passAndSign (signatureReceivedSize: Int, payload: XyoBuff) : Deferred<XyoIterableObject> = GlobalScope.async {
        val toSend = ArrayList<XyoBuff>()

        signForSelf(payload).await()

        val publicKeyIt = this@XyoZigZagBoundWitness[XyoSchemas.FETTER.id]
        for (i in signatureReceivedSize + 1 until publicKeyIt.size ) {
            toSend.add(publicKeyIt[i])
        }

        val signatureIt = this@XyoZigZagBoundWitness[XyoSchemas.WITNESSS.id]
        toSend.add(signatureIt[signatureIt.size - 1])

        return@async XyoIterableObject.createUntypedIterableObject(
                schema,
                toSend.toTypedArray()
        )
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
        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, publicKeys.toTypedArray())
    }

    private fun signBoundWitness (payload: XyoBuff) = GlobalScope.async {
        return@async createWitness(XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, Array(signers.size) { i ->
            signCurrent(signers[i]).await()
        }), payload)
    }

    private fun signForSelf (payload: XyoBuff) = GlobalScope.async {
        val signatureSet = signBoundWitness(payload).await()
        dynamicLeader.add(signatureSet)
    }
}