package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import java.util.*

/**
 * A zig-zag bound witness protocol.
 *
 * @param signers the signers to sign the bound witness with.
 * @param payload the payload to pur in the bound witness.
 */

open class XyoZigZagBoundWitness(private val signers : Array<XyoSigner>,
                                 private val payload : ByteArray) : XyoBoundWitness() {

    private val dynamicPayloads = ArrayList<ByteArray>()
    private val dynamicPublicKeys = ArrayList<ByteArray>()
    private val dynamicSignatureSets = ArrayList<ByteArray>()

    override val payloads: ByteArray
        get() = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED,
                XyoObjectSetCreator.convertObjectsToType(dynamicPayloads.toTypedArray(), XyoSchemas.PAYLOAD))

    override val publicKeys: ByteArray
        get() = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED,
                XyoObjectSetCreator.convertObjectsToType(dynamicPublicKeys.toTypedArray(), XyoSchemas.ARRAY_UNTYPED))

    override val signatures: ByteArray
        get() = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED,
                XyoObjectSetCreator.convertObjectsToType(dynamicSignatureSets.toTypedArray(), XyoSchemas.ARRAY_UNTYPED))

    private var hasSentKeysAndPayload = false

    /**
     * Adds data to the bound witness and returns whats the party should send back.
     *
     * @param transfer The data from the other party.
     * @param endPoint If not already turned around, decide if what to send sign and send back.
     * @return A XyoBoundWitnessTransfer to send to the other party.
     */
    fun incomingData (transfer : ByteArray?, endPoint : Boolean) : Deferred<ByteArray> = GlobalScope.async {
        if (transfer != null) {
            addTransfer(transfer).await()
        }

        if (!hasSentKeysAndPayload) {
            dynamicPublicKeys.add(makeSelfKeySet())
            dynamicPayloads.add(payload)
            hasSentKeysAndPayload = true
        }

        if (XyoIterableObject(signatures).size != XyoIterableObject(publicKeys).size) {
            return@async getReturnFromIncoming(getNumberOfSignaturesFromTransfer(transfer), endPoint).await()

        }

        return@async XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
    }

    private fun getNumberOfSignaturesFromTransfer (transfer: ByteArray?) : Int {
        if (transfer == null) {
            return 0
        }

        return XyoIterableObject((XyoIterableObject(transfer)[2])).size
    }

    private fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean) : Deferred<ByteArray> = GlobalScope.async {

        if (XyoIterableObject(signatures).size == 0 && !endPoint) {
            return@async self
        }

        return@async passAndSign(signatureReceivedSize).await()
    }

    private fun passAndSign (signatureReceivedSize: Int) : Deferred<ByteArray> = GlobalScope.async {
        val keysToSend = ArrayList<ByteArray>()
        val payloadsToSend = ArrayList<ByteArray>()
        val signatureToSend = ArrayList<ByteArray>()

        signForSelf().await()

        val publicKeyIt = XyoIterableObject(publicKeys)
        for (i in signatureReceivedSize + 1 until publicKeyIt.size ) {
            keysToSend.add(publicKeyIt[i])
        }

        val payloadIt = XyoIterableObject(payloads)
        for (i in signatureReceivedSize + 1 until payloadIt.size) {
            payloadsToSend.add(payloadIt[i])
        }

        val signatureIt =  XyoIterableObject(signatures).iterator
        signatureToSend.add(signatureIt.next())

        return@async XyoObjectSetCreator.createUntypedIterableObject(
                schema,
                arrayOf(createKeySet(keysToSend.toTypedArray()), createPayloads(payloadsToSend.toTypedArray()), createSignatures(signatureToSend.toTypedArray()))
        )
    }

    private fun createKeySet (keys : Array<ByteArray>) : ByteArray {
        return XyoObjectSetCreator.createUntypedIterableObject(
                XyoSchemas.ARRAY_UNTYPED,
                keys
        )
    }

    private fun createPayloads (payloads : Array<ByteArray>) : ByteArray {
        return XyoObjectSetCreator.createTypedIterableObject(
                XyoSchemas.ARRAY_TYPED,
                payloads
        )
    }

    private fun createSignatures (signatures : Array<ByteArray>) : ByteArray {
        return XyoObjectSetCreator.createUntypedIterableObject(
                XyoSchemas.ARRAY_UNTYPED,
                signatures
        )
    }

    private fun addTransfer (transfer : ByteArray) : Deferred<Unit> = GlobalScope.async {
        val it = XyoIterableObject(transfer)
        addIncomingKeys(XyoIterableObject(it[0]).iterator)
        addIncomingPayload(XyoIterableObject(it[1]).iterator)
        addIncomingSignatures(XyoIterableObject(it[2]).iterator)
    }

    private fun addIncomingKeys(incomingKeySets : Iterator<ByteArray>) {
        for (item in incomingKeySets) {
            dynamicPublicKeys.add(item)
        }
    }

    private fun addIncomingPayload(incomingPayloads : Iterator<ByteArray>) {
        for (item in incomingPayloads) {
            dynamicPayloads.add(item)
        }
    }

    private fun addIncomingSignatures(incomingSignatures : Iterator<ByteArray>) {
       for (item in incomingSignatures) {
           if (dynamicSignatureSets.size != 0) {
               dynamicSignatureSets.add(0, item)
               return
           }
           dynamicSignatureSets.add(item)
       }
    }

    private fun makeSelfKeySet() : ByteArray {
        val publicKeys = ArrayList<ByteArray>()
        for (signer in signers) {
            publicKeys.add(signer.publicKey.self)
        }
        return XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, publicKeys.toTypedArray())
    }

    private fun signBoundWitness () = GlobalScope.async {
        return@async createSignatures(Array(signers.size) { i -> signCurrent(signers[i]).await() })
    }

    private fun signForSelf () = GlobalScope.async {
        val signatureSet = signBoundWitness().await()
        addIncomingSignatures(arrayOf(signatureSet).iterator())
    }
}