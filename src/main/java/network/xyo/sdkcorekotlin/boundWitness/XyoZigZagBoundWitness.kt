package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigner
import java.util.*

/**
 * A zig-zag bound witness protocol.
 *
 * @param signers the signers to sign the bound witness with.
 * @param payload the payload to pur in the bound witness.
 */
open class XyoZigZagBoundWitness(private val signers : Array<XyoSigner>,
                                 private val payload : XyoPayload) : XyoBoundWitness() {

    private val dynamicPayloads = ArrayList<XyoPayload>()
    private val dynamicPublicKeys = ArrayList<XyoKeySet>()
    private val dynamicSignatureSets = ArrayList<XyoSignatureSet>()

    override val payloads: Array<XyoPayload>
        get() = dynamicPayloads.toTypedArray()

    override val publicKeys: Array<XyoKeySet>
        get() = dynamicPublicKeys.toTypedArray()

    override val signatures: Array<XyoSignatureSet>
        get() = dynamicSignatureSets.toTypedArray()

    private var hasSentKeysAndPayload = false

    /**
     * Adds data to the bound witness and returns whats the party should send back.
     *
     * @param transfer The data from the other party.
     * @param endPoint If not already turned around, decide if what to send sign and send back.
     * @return A XyoBoundWitnessTransfer to send to the other party.
     */
    fun incomingData (transfer : XyoBoundWitnessTransfer?, endPoint : Boolean) = GlobalScope.async {
        updateObjectCache()

        if (transfer != null) {
            addTransfer(transfer).await()
        }

        if (!hasSentKeysAndPayload) {
            dynamicPublicKeys.add(makeSelfKeySet())
            dynamicPayloads.add(payload)
            hasSentKeysAndPayload = true
        }

        if (signatures.size != publicKeys.size) {
            val signatureReceivedSize = transfer?.signatureToSend?.size ?: 0
            return@async getReturnFromIncoming(signatureReceivedSize, endPoint).await()
        }

        return@async XyoBoundWitnessTransfer(arrayOf(), arrayOf(), arrayOf())
    }

    private fun getReturnFromIncoming (signatureReceivedSize : Int, endPoint: Boolean) : Deferred<XyoBoundWitnessTransfer> = GlobalScope.async {
        val keysToSend = ArrayList<XyoObject>()
        val payloadsToSend = ArrayList<XyoObject>()
        val signatureToSend = ArrayList<XyoObject>()

        if (signatures.isEmpty() && !endPoint) {
            for (key in publicKeys) {
                keysToSend.add(key)
            }

            for (payload in payloads) {
                payloadsToSend.add(payload)
            }
        } else {
            signForSelf().await()
            for (i in signatureReceivedSize + 1 until publicKeys.size ) {
                keysToSend.add(publicKeys[i])
            }

            for (i in signatureReceivedSize + 1 until payloads.size) {
                payloadsToSend.add(payloads[i])
            }

            for (i in 0 until signatures.size) {
                signatureToSend.add(signatures[i])
            }

        }

        return@async XyoBoundWitnessTransfer(keysToSend.toTypedArray(), payloadsToSend.toTypedArray(), signatureToSend.toTypedArray())
    }

    private fun addTransfer (transfer : XyoBoundWitnessTransfer) = GlobalScope.async {
        addIncomingKeys(transfer.keysToSend)
        addIncomingPayload(transfer.payloadsToSend)
        addIncomingSignatures(transfer.signatureToSend)
    }

    private fun addIncomingKeys(incomingKeySets : Array<XyoObject>) {
        for (i in 0 until incomingKeySets.size) {
            val incomingKeySet = incomingKeySets[i] as? XyoKeySet
            if (incomingKeySet != null) {
                dynamicPublicKeys.add(incomingKeySet)
            }
        }
    }

    private fun addIncomingPayload(incomingPayloads : Array<XyoObject>) {
        for (i in 0 until incomingPayloads.size) {
            val incomingPayload = incomingPayloads[i] as? XyoPayload
            if (incomingPayload != null) {
                dynamicPayloads.add(incomingPayload)
            }
        }
    }

    private fun addIncomingSignatures(incomingSignatures : Array<XyoObject>) {
        for (i in 0 until incomingSignatures.size) {
            val incomingSignatureSet = incomingSignatures[i] as? XyoSignatureSet
            if (incomingSignatureSet != null) {
                if (dynamicSignatureSets.size != 0) {
                    dynamicSignatureSets.add(0, incomingSignatureSet)
                    return
                }
                dynamicSignatureSets.add(incomingSignatureSet)
            }
        }
    }

    private fun makeSelfKeySet() : XyoKeySet {
        val publicKeys = ArrayList<XyoObject>()
        for (signer in signers) {
            publicKeys.add(signer.publicKey)
        }
        return XyoKeySet(publicKeys.toTypedArray())
    }

    private fun signBoundWitness () = GlobalScope.async {
        return@async XyoSignatureSet(Array(signers.size) { i -> signCurrent(signers[i]).await() })
    }

    private fun signForSelf () = GlobalScope.async {
        val signatureSet = signBoundWitness().await()
        addIncomingSignatures(arrayOf(signatureSet))
    }
}