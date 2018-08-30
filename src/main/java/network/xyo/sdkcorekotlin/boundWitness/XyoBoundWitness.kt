package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigner
import java.nio.ByteBuffer

abstract class XyoBoundWitness : XyoObject() {
    abstract val publicKeys : Array<XyoKeySet>
    abstract val payloads : Array<XyoPayload>
    abstract val signatures : Array<XyoSignatureSet>

    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult(4)

    override val data: XyoResult<ByteArray>
        get() = makeBoundWitness()

    fun getHash (hashCreator : XyoHash.XyoHashProvider) = async {
        val dataToHash = getSigningData()
        val dataToHashValue = dataToHash.value ?: return@async XyoResult<XyoHash>(dataToHash.error ?: XyoError("Cant get hash!"))
        return@async hashCreator.createHash(dataToHashValue).await()
    }

    fun signCurrent (signer: XyoSigner) = async {
        val dataToSign = getSigningData()
        val dataToSignValue = dataToSign.value ?: return@async XyoResult<XyoObject>(XyoError(""))
        return@async signer.signData(dataToSignValue).await()
    }

    private fun getSigningData () : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(payloads.size + 1)
        val makePublicKeysUntyped = makePublicKeys().untyped
        val makePublicKeysUntypedValue = makePublicKeysUntyped.value ?: return XyoResult(XyoError("makePublicKeysUntypedValue is null!"))

        setter.add(makePublicKeysUntypedValue, 0)

        for (i in 0 until payloads.size) {
            val payload = payloads[i] as? XyoPayload
            payload ?: return XyoResult(XyoError("Payload cant cant to XyoPayload"))
            val payloadValue = payload.signedPayload.untyped.value ?: return XyoResult<ByteArray>(XyoError("Payload is null!"))
            setter.add(payloadValue, i + 1)
        }

        return XyoResult(setter.merge())
    }

    private fun makeBoundWitness() : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(3)
        val makePublicKeysUntyped = makePublicKeys().untyped
        val makeSignaturesUntyped = makeSignatures().untyped
        val makePayloadsUntyped = makePayloads().untyped

        if (makePublicKeysUntyped.error != null) return XyoResult(makePublicKeysUntyped.error ?: XyoError("Unknown makePublicKeysUntyped error!"))
        if (makeSignaturesUntyped.error != null) return XyoResult(makeSignaturesUntyped.error ?: XyoError("Unknown makeSignaturesUntyped error!"))
        if (makePayloadsUntyped.error != null) return XyoResult(makePayloadsUntyped.error ?: XyoError("Unknown makePayloadsUntyped error!"))

        val makePublicKeysUntypedValue = makePublicKeysUntyped.value ?: return XyoResult(XyoError("makePublicKeysUntypedValue is null!"))
        val makePayloadsUntypedValue = makePayloadsUntyped.value ?: return XyoResult(XyoError("makePayloadsUntypedValue is null!"))
        val makeSignaturesUntypedValue = makeSignaturesUntyped.value ?: return XyoResult(XyoError("makeSignaturesUntypedValue is null!"))

        setter.add(makePublicKeysUntypedValue, 0)
        setter.add(makePayloadsUntypedValue, 1)
        setter.add(makeSignaturesUntypedValue, 2)
        return XyoResult(setter.merge())

    }

    private fun makePublicKeys () : XyoSingleTypeArrayShort {
        return XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor,  Array(publicKeys.size, { i -> publicKeys[i] as XyoObject }))
    }

    private fun makeSignatures () : XyoSingleTypeArrayShort {
        return XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, Array(signatures.size, { i -> signatures[i] as XyoObject }))
    }

    private fun makePayloads () : XyoSingleTypeArrayInt {
        return XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, Array(payloads.size, { i -> payloads[i] as XyoObject }))
    }

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x01
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(4)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val shortArrayReadSize = XyoSingleTypeArrayShort.sizeOfBytesToGetSize
            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize
            val shortArrayReadSizeValue = shortArrayReadSize.value ?: return XyoResult(XyoError("shortArrayReadSizeValue is null!"))
            val intArrayReadSizeValue = intArrayReadSize.value ?: return XyoResult(XyoError("intArrayReadSizeValue is null!"))

            return unpackIntoEncodedArrays(byteArray, shortArrayReadSizeValue, intArrayReadSizeValue)

        }

        private fun unpackIntoEncodedArrays(byteArray: ByteArray, shortArrayReadSize: Int, intArrayReadSize: Int): XyoResult<XyoObject> {
            val byteReader = XyoByteArrayReader(byteArray)

            val keySetArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(4, shortArrayReadSize))
            if (keySetArraySize.error != null) return XyoResult(XyoError(""))
            val keySetArraySizeValue = keySetArraySize.value ?: return XyoResult(XyoError(""))
            val keySets = getKeySetsArray(byteReader.read(4, keySetArraySizeValue))
            val keySetsValue = keySets.value ?: return XyoResult(XyoError("keySetsValue is null!"))

            val payloadArraySize = XyoSingleTypeArrayInt.readSize(byteReader.read(keySetArraySizeValue + 4, intArrayReadSize))
            if (payloadArraySize.error != null)  return XyoResult(XyoError(""))
            val payloadArraySizeValue = payloadArraySize.value ?: return XyoResult(XyoError(""))
            val payloads = getPayloadsArray(byteReader.read(keySetArraySizeValue + 4, payloadArraySizeValue))
            val payloadsValue = payloads.value ?: return XyoResult(XyoError("payloadsValue is null!"))

            val signatureArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(keySetArraySizeValue + payloadArraySizeValue + 4, shortArrayReadSize))
            if (signatureArraySize.error != null)  return XyoResult(XyoError(""))
            val signatureArraySizeValue = signatureArraySize.value ?: return XyoResult(XyoError(""))
            val signatures = getSignatureArray(byteReader.read(keySetArraySizeValue + payloadArraySizeValue + 4, signatureArraySizeValue))
            val signaturesValue = signatures.value ?: return XyoResult(XyoError("signaturesValue is null!"))

            return unpackFromArrays(keySetsValue, payloadsValue, signaturesValue)
        }


        private fun getKeySetsArray(bytes: ByteArray): XyoResult<Array<XyoKeySet>> {
            val keySetArray = XyoSingleTypeArrayShort.createFromPacked(bytes)
            val keySetArrayValue = keySetArray.value as? XyoSingleTypeArrayShort
            if (keySetArrayValue != null && keySetArray.error == null) {
                return XyoResult(Array(keySetArrayValue.size, { i -> keySetArrayValue.array[i] as XyoKeySet }))
            }
            return XyoResult(keySetArray.error ?: XyoError("Unknown XyoSingleTypeArrayShort Error!"))
        }

        private fun getPayloadsArray(bytes: ByteArray): XyoResult<Array<XyoPayload>> {
            val payloadArray = XyoSingleTypeArrayInt.createFromPacked(bytes)
            val payloadArrayValue = payloadArray.value as? XyoSingleTypeArrayInt
            if (payloadArrayValue != null && payloadArray.error == null) {
                return XyoResult(Array(payloadArrayValue.size, { i -> payloadArrayValue.array[i] as XyoPayload }))
            }
            return XyoResult(payloadArray.error ?: XyoError("Unknown XyoSingleTypeArrayInt Error!"))
        }

        private fun getSignatureArray(bytes: ByteArray): XyoResult<Array<XyoSignatureSet>> {
            val signatureArray = XyoSingleTypeArrayShort.createFromPacked(bytes)
            val signatureArrayValue = signatureArray.value as? XyoSingleTypeArrayShort
            if (signatureArrayValue != null && signatureArray.error == null) {
                return XyoResult(Array(signatureArrayValue.size, { i -> signatureArrayValue.array[i] as XyoSignatureSet }))
            }
            return XyoResult(signatureArray.error ?: XyoError("Unknown XyoSingleTypeArrayShort Error!"))
        }

        private fun unpackFromArrays(keysets : Array<XyoKeySet>, payloads: Array<XyoPayload>, signatures: Array<XyoSignatureSet>): XyoResult<XyoObject> {
            return XyoResult(object : XyoBoundWitness() {
                override val payloads: Array<XyoPayload>
                    get() = payloads

                override val publicKeys: Array<XyoKeySet>
                    get() = keysets

                override val signatures: Array<XyoSignatureSet>
                    get() = signatures
            })
        }

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).int)
        }
    }
}
