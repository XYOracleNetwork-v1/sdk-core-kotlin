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
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import java.nio.ByteBuffer

abstract class XyoBoundWitness : XyoObject() {
    abstract val publicKeys : Array<XyoKeySet>
    abstract val payloads : Array<XyoPayload>
    abstract val signatures : Array<XyoSignatureSet>

    override val data: XyoResult<ByteArray>
        get() = makeBoundWitness()

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult(4)

    fun getHash (hashCreator : XyoHash.XyoHashCreator) = async {
        val dataToHash = getSigningData()
        val dataToHashValue = dataToHash.value ?: return@async XyoResult<XyoHash>(dataToHash.error!!)
        return@async hashCreator.createHash(dataToHashValue).await()
    }

    private fun makeBoundWitness() : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(3)
        val makePublicKeysUntyped = makePublicKeys().untyped
        val makeSignaturesUntyped = makeSignatures().untyped
        val makePayloadsUntyped = makePayloads().untyped

        if (makePublicKeysUntyped.error == null && makePublicKeysUntyped.value != null) {
            if (makeSignaturesUntyped.error == null && makeSignaturesUntyped.value != null) {
                if (makePayloadsUntyped.error == null && makePayloadsUntyped.value != null) {
                    setter.add(makePublicKeysUntyped.value!!, 0)
                    setter.add(makePayloadsUntyped.value!!, 1)
                    setter.add(makeSignaturesUntyped.value!!, 2)
                    return XyoResult(setter.merge())
                }
                return XyoResult(XyoError(""))
            }
            return XyoResult(XyoError(""))
        }
        return XyoResult(XyoError(""))
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

    fun signCurrent (signer: XyoSigningObject) = async {
        val dataToSign = getSigningData()
        val dataToSignValue = dataToSign.value ?: return@async XyoResult<XyoObject>(XyoError(""))
        return@async signer.signData(dataToSignValue).await()
    }

    private fun getSigningData () : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(payloads.size + 1)
        val makePublicKeysUntyped = makePublicKeys().untyped
        if (makePublicKeysUntyped.error == null && makePublicKeysUntyped.value != null) {
            setter.add(makePublicKeysUntyped.value!!, 0)
            for (i in 0 until payloads.size) {
                val payload = payloads[i] as? XyoPayload
                if (payload != null) {
                    val payloadValue = payload.signedPayload.untyped.value
                    if (payloadValue != null) {
                        setter.add(payloadValue, i + 1)
                    } else {
                        return XyoResult<ByteArray>(XyoError("1"))
                    }
                } else {
                    return XyoResult<ByteArray>(XyoError("2"))
                }
            }
            return XyoResult(setter.merge())
        }
        return XyoResult<ByteArray>(XyoError("3"))
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x01

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(4)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val shortArrayReadSize = XyoSingleTypeArrayShort.sizeOfBytesToGetSize
            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize

            if (shortArrayReadSize.error == null && shortArrayReadSize.value != null) {
                if (intArrayReadSize.error == null && intArrayReadSize.value != null) {
                    unpackIntoEncodedArrays(byteArray, shortArrayReadSize.value!!, intArrayReadSize.value!!)
                }
                return XyoResult(XyoError(""))
            }
            return XyoResult(XyoError(""))
        }

        private fun unpackIntoEncodedArrays(byteArray: ByteArray, shortArrayReadSize: Int, intArrayReadSize: Int): XyoResult<XyoObject> {
            val byteReader = XyoByteArrayReader(byteArray)

            val keySetArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(0, shortArrayReadSize))
            if (keySetArraySize.error != null)  return XyoResult(XyoError(""))
            val keySetArraySizeValue = keySetArraySize.value ?: return XyoResult(XyoError(""))
            val keySets = getKeySetsArray(byteReader.read(0, keySetArraySizeValue))

            val payloadArraySize = XyoSingleTypeArrayInt.readSize(byteReader.read(keySetArraySizeValue, intArrayReadSize))
            if (payloadArraySize.error != null)  return XyoResult(XyoError(""))
            val payloadArraySizeValue = payloadArraySize.value ?: return XyoResult(XyoError(""))
            val payloads = getPayloadsArray(byteReader.read(keySetArraySizeValue, payloadArraySizeValue))

            val signatureArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(keySetArraySizeValue + payloadArraySizeValue, shortArrayReadSize))
            if (signatureArraySize.error != null)  return XyoResult(XyoError(""))
            val signatureArraySizeValue = signatureArraySize.value ?: return XyoResult(XyoError(""))
            val signatures = getSignatureArray(byteReader.read(keySetArraySizeValue + payloadArraySizeValue, signatureArraySizeValue))

            return unpackFromArrays(keySets.value!!, payloads.value!!, signatures.value!!)
        }



        private fun getKeySetsArray(bytes: ByteArray): XyoResult<Array<XyoKeySet>> {
            val keySetArray = XyoSingleTypeArrayShort.createFromPacked(bytes)
            val keySetArrayValue = keySetArray.value as? XyoSingleTypeArrayShort
            if (keySetArrayValue != null) {
                return XyoResult(Array(keySetArrayValue.size, { i -> keySetArrayValue.array[i] as XyoKeySet }))
            }
            return XyoResult(XyoError(""))
        }

        private fun getPayloadsArray(bytes: ByteArray): XyoResult<Array<XyoPayload>> {
            val payloadArray = XyoSingleTypeArrayInt.createFromPacked(bytes)
            val payloadArrayValue = payloadArray.value as? XyoSingleTypeArrayInt
            if (payloadArrayValue != null) {
                return XyoResult(Array(payloadArrayValue.size, { i -> payloadArrayValue.array[i] as XyoPayload }))
            }
            return XyoResult(XyoError(""))
        }

        private fun getSignatureArray(bytes: ByteArray): XyoResult<Array<XyoSignatureSet>> {
            val signatureArray = XyoSingleTypeArrayInt.createFromPacked(bytes)
            val signatureArrayValue = signatureArray.value as? XyoSingleTypeArrayInt
            if (signatureArrayValue != null) {
                return XyoResult(Array(signatureArrayValue.size, { i -> signatureArrayValue.array[i] as XyoSignatureSet }))
            }
            return XyoResult(XyoError(""))
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
