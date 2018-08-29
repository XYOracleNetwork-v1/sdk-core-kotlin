package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigningObject

class XyoZigZagBoundWitness(private val signers : Array<XyoSigningObject>,
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

    fun incomingData (transfer : XyoBoundWitnessTransfer?, endPoint : Boolean) = async {
        val keysToSend = ArrayList<XyoObject>()
        val payloadsToSend = ArrayList<XyoObject>()
        val signatureToSend = ArrayList<XyoObject>()
        val signatureReceivedSize = transfer?.signatureToSend?.size ?: 0

        if (transfer != null) {
            addTransfer(transfer).await()
        }

        if (!hasSentKeysAndPayload) {
            dynamicPublicKeys.add(makeSelfKeySet())
            dynamicPayloads.add(payload)
            hasSentKeysAndPayload = true
        }

        if (signatures.size != publicKeys.size) {
            if (signatures.size == 0 && !endPoint) {
                for (key in publicKeys) {
                    keysToSend.add(key)
                }

                for (payload in payloads) {
                    payloadsToSend.add(payload)
                }
            } else {
                signForSelf().await()

                for (i in signatureReceivedSize + 1..publicKeys.size - 1) {
                    keysToSend.add(publicKeys[i])
                }

                for (i in signatureReceivedSize + 1..payloads.size - 1) {
                    payloadsToSend.add(payloads[i])
                }

                for (i in signatureReceivedSize..signatures.size - 1) {
                    signatureToSend.add(signatures[i])
                }
            }
        }

        return@async XyoResult(XyoBoundWitnessTransfer(keysToSend.toTypedArray(), payloadsToSend.toTypedArray(), signatureToSend.toTypedArray()))
    }

    private fun addTransfer (transfer : XyoBoundWitnessTransfer) = async {
        val keyError = addIncomingKeys(transfer.keysToSend)
        if (keyError != null) {
            return@async XyoResult<XyoBoundWitnessTransfer>(keyError)
        }

        val payloadError = addIncomingPayload(transfer.payloadsToSend)
        if (payloadError != null) {
            return@async XyoResult<XyoBoundWitnessTransfer>(keyError)
        }

        val signatureError = addIncomingSignatures(transfer.signatureToSend)
        if (signatureError != null) {
            return@async XyoResult<XyoBoundWitnessTransfer>(keyError)
        }
        return@async null
    }

    private fun addIncomingKeys(incomingKeySets : Array<XyoObject>) : XyoError? {
        for (i in 0..incomingKeySets.size - 1) {
            val incomingKeySet = incomingKeySets[i] as? XyoKeySet
            if (incomingKeySet != null) {
                dynamicPublicKeys.add(incomingKeySet)
            } else {
                return XyoError("Error Unpacking KeySet!")
            }
        }
        return null
    }

    private fun addIncomingPayload(incomingPayloads : Array<XyoObject>) : XyoError? {
        for (i in 0..incomingPayloads.size - 1) {
            val incomingPayload = incomingPayloads[i] as? XyoPayload
            if (incomingPayload != null) {
                dynamicPayloads.add(incomingPayload)
            } else {
                return XyoError("Error Unpacking Payload!")
            }
        }
        return null
    }

    private fun addIncomingSignatures(incomingSignatures : Array<XyoObject>) : XyoError? {
        for (i in 0..incomingSignatures.size - 1) {
            val incomingSignatureSet = incomingSignatures[i] as? XyoSignatureSet
            if (incomingSignatureSet != null) {
                dynamicSignatureSets.add(incomingSignatureSet)
            } else {
                return XyoError("Error Unpacking SignatureSet!")
            }
        }
        return null
    }

    private fun makeSelfKeySet() : XyoKeySet {
        return XyoKeySet(Array(signers.size, {i ->
            signers[i].publicKey
        }))
    }

    private fun signBoundWitness () = async {
        val signatureSet = XyoSignatureSet(Array(signers.size, { i ->
            val signature = signCurrent(signers[i]).await()
            if (signature.error == null && signature.value != null) {
                signature.value!!
            } else {
                return@async XyoResult<XyoSignatureSet>(XyoError("Error: ${signature.error}, Value: ${signature.value}"))
            }
        }))
        return@async XyoResult(signatureSet)
    }

    private fun signForSelf () = async {
        val signatureSet = signBoundWitness().await()
        if (signatureSet.error == null && signatureSet.value != null) {
            dynamicSignatureSets.add(signatureSet.value!!)
            return@async true
        }

        return@async false
    }
}