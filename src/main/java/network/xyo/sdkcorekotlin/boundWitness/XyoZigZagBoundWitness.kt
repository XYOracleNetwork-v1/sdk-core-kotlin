package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectIterator
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
        get() = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED, dynamicPayloads.toTypedArray())

    override val publicKeys: ByteArray
        get() = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED, dynamicPublicKeys.toTypedArray())

    override val signatures: ByteArray
        get() = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED, dynamicSignatureSets.toTypedArray())

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

        if (XyoObjectIterator(signatures).size != XyoObjectIterator(publicKeys).size) {
            return@async getReturnFromIncoming(getNumberOfSignaturesFromTransfer(transfer), endPoint).await()

        }

        return@async XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
    }

    private fun getNumberOfSignaturesFromTransfer (transfer: ByteArray?) : Int {
        if (transfer == null) {
            return 0
        }

        return XyoObjectIterator((XyoObjectIterator(transfer)[2])).size
    }

    private fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean) : Deferred<ByteArray> = GlobalScope.async {

        if (XyoObjectIterator(signatures).size == 0 && !endPoint) {
            return@async self
        }

        return@async passAndSign(signatureReceivedSize).await()
    }

    private fun passAndSign (signatureReceivedSize: Int) : Deferred<ByteArray> = GlobalScope.async {
        val keysToSend = ArrayList<ByteArray>()
        val payloadsToSend = ArrayList<ByteArray>()
        val signatureToSend = ArrayList<ByteArray>()

        signForSelf().await()

        val publicKeyIt = XyoObjectIterator(publicKeys)
        for (i in signatureReceivedSize + 1 until publicKeyIt.size ) {
            keysToSend.add(publicKeyIt[i])
        }

        val payloadIt = XyoObjectIterator(payloads)
        for (i in signatureReceivedSize + 1 until payloadIt.size) {
            payloadsToSend.add(payloadIt[i])
        }

        val signatureIt =  XyoObjectIterator(signatures)
        signatureToSend.add(signatureIt.next())

        return@async XyoObjectSetCreator.createUntypedIterableObject(
                schema,
                arrayOf(createKeySet(keysToSend.toTypedArray()), createPayloads(payloadsToSend.toTypedArray()), createSignatures(signatureToSend.toTypedArray()))
        )
    }

    private fun createKeySet (keys : Array<ByteArray>) : ByteArray {
        return XyoObjectSetCreator.createUntypedIterableObject(
                XyoSchemas.KEY_SET,
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
                XyoSchemas.SIGNATURE_SET,
                signatures
        )
    }

    private fun addTransfer (transfer : ByteArray) : Deferred<Unit> = GlobalScope.async {
        val it = XyoObjectIterator(transfer)
        addIncomingKeys(XyoObjectIterator(it[0]))
        addIncomingPayload(XyoObjectIterator(it[1]))
        addIncomingSignatures(XyoObjectIterator(it[2]))
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
        return XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.KEY_SET, publicKeys.toTypedArray())
    }

    private fun signBoundWitness () = GlobalScope.async {
        return@async createSignatures(Array(signers.size) { i -> signCurrent(signers[i]).await() })
    }

    private fun signForSelf () = GlobalScope.async {
        val signatureSet = signBoundWitness().await()
        addIncomingSignatures(arrayOf(signatureSet).iterator())
    }
}