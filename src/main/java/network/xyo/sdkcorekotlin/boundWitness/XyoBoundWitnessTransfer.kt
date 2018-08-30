package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import java.nio.ByteBuffer

class XyoBoundWitnessTransfer(val keysToSend : Array<XyoObject>,
                              val payloadsToSend : Array<XyoObject>,
                              val signatureToSend : Array<XyoObject>) : XyoObject() {

    override val data: XyoResult<ByteArray> = makeEncoded()
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult(4)

    private val stage : Byte
        get() {
            if (keysToSend.isNotEmpty() && payloadsToSend.isNotEmpty() && signatureToSend.isEmpty()) {
                return 0x01
            } else if (keysToSend.isNotEmpty() && payloadsToSend.isNotEmpty() && signatureToSend.isNotEmpty()) {
                return 0x02
            } else if (keysToSend.isEmpty() && payloadsToSend.isEmpty() && signatureToSend.isNotEmpty()) {
                return 0x03
            }
            return 0x01
        }

    private fun makeWithKeysAndPayload () : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(3)
        setter.add(byteArrayOf(stage), 0)



        val encodedKeySets = XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped
        val encodedKeySetsValue = encodedKeySets.value ?: return XyoResult(XyoError(
                this.toString(),
                "Packing keySet returned null!")
        )
        if (encodedKeySets.error != null) return XyoResult(encodedKeySets.error ?: XyoError(
                this.toString(),
                "Unknown encodedKeySets error!")
        )
        setter.add(encodedKeySetsValue, 1)



        val encodedPayloads = XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped
        val encodedPayloadsValue = encodedPayloads.value ?: return XyoResult(XyoError(
                this.toString(),
                "Unpacking payloads returned null!")
        )
        if (encodedPayloads.error != null) return XyoResult(encodedPayloads.error ?: XyoError(
                this.toString(),
                "Unknown encodedPayloads error!")
        )
        setter.add(encodedPayloadsValue, 2)



        return XyoResult(setter.merge())
    }

    private fun makeWithEverything() : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(4)
        setter.add(byteArrayOf(stage), 0)



        val encodedKeySets = XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped
        val encodedKeySetsValue = encodedKeySets.value ?: return XyoResult(XyoError(
                this.toString(),
                "Packing keySet returned null!")
        )
        if (encodedKeySets.error != null) return XyoResult(encodedKeySets.error ?: XyoError(
                this.toString(),
                "Unknown encodedKeySets error!")
        )
        setter.add(encodedKeySetsValue, 1)



        val encodedPayloads = XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped
        val encodedPayloadsValue = encodedPayloads.value ?: return XyoResult(XyoError(
                this.toString(),
                "Packing payloads returned null!")
        )
        if (encodedPayloads.error != null) return XyoResult(encodedPayloads.error ?: XyoError(
                this.toString(),
                "Unknown encodedPayloads error!")
        )
        setter.add(encodedPayloadsValue, 2)



        val encodedSignatures = XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped
        val encodedSignaturesValue = encodedSignatures.value ?: return XyoResult(XyoError(
                this.toString(),
                "Packing signatures returned null!")
        )
        if (encodedSignatures.error != null) return XyoResult(encodedSignatures.error ?: XyoError(
                this.toString(),
                "Unknown encodedSignatures error!")
        )
        setter.add(encodedSignaturesValue, 3)


        return XyoResult(setter.merge())
    }

    private fun makeWithSignatures() : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(2)
        setter.add(byteArrayOf(stage), 0)



        val encodedSignatures = XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped
        val encodedSignaturesValue = encodedSignatures.value ?:  return XyoResult(XyoError(
                this.toString(),
                "Packing signatures returned null!")
        )
        if (encodedSignatures.error != null) return XyoResult(encodedSignatures.error ?: XyoError(
                this.toString(),
                "Unknown encodedSignatures error!")
        )
        setter.add(encodedSignaturesValue, 3)



        return XyoResult(setter.merge())
    }

    private fun makeEncoded () : XyoResult<ByteArray> {
        when (stage) {
            0x01.toByte() -> return makeWithKeysAndPayload()
            0x02.toByte() -> return makeWithEverything()
            0x03.toByte() -> return makeWithSignatures()
        }
        return XyoResult(XyoError(this.toString(),"Unknown stage!"))
    }

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x08
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(4)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val stage = byteArray[4]
            return unpackEverything(byteArray, 5, stage)
        }

        private fun unpackEverything(bytes: ByteArray, globalOffset : Int, type: Byte) : XyoResult<XyoObject> {
            val byteReader = XyoByteArrayReader(bytes)
            var currentOffset = globalOffset

            var keySetArray : XyoSingleTypeArrayShort? = null
            var payloadArray : XyoSingleTypeArrayInt? = null
            var signatureArray :  XyoSingleTypeArrayShort? = null



            val shortArrayReadSize = XyoSingleTypeArrayShort.sizeOfBytesToGetSize
            if (shortArrayReadSize.error != null) return XyoResult(shortArrayReadSize.error ?: XyoError(
                    this.toString(),
                    "Unknown short array size read error!")
            )
            val shortArrayReadSizeValue = shortArrayReadSize.value ?: return XyoResult(XyoError(
                    this.toString(),
                    "Unpacking short array size returned null!")
            )



            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize
            if (intArrayReadSize.error != null) return XyoResult(intArrayReadSize.error ?: XyoError(
                    this.toString(),
                    "Unknown int array size read error!")
            )
            val intArrayReadSizeValue = intArrayReadSize.value ?: return XyoResult(XyoError(
                    this.toString(),
                    "Unpacking int array size returned null!")
            )



            if (type == 0x01.toByte() || type == 0x02.toByte()) {

                val keySetArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(currentOffset, shortArrayReadSizeValue))
                if (keySetArraySize.error != null) return XyoResult(keySetArraySize.error ?: XyoError(
                        this.toString(),
                        "Unknown keySet size read error!")
                )
                val keySetArraySizeValue = keySetArraySize.value ?: return XyoResult(XyoError(
                        this.toString(),
                        "Unpacking keySets returned null!")
                )
                val keySetArrayResult = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(currentOffset, keySetArraySizeValue))
                if (keySetArrayResult.error != null) return XyoResult(keySetArrayResult.error ?: XyoError(
                        this.toString(),
                        "Unknown keySet read error!")
                )
                keySetArray = keySetArrayResult.value as? XyoSingleTypeArrayShort
                currentOffset += keySetArraySizeValue




                val payloadArraySize  = XyoSingleTypeArrayInt.readSize(byteReader.read(currentOffset, intArrayReadSizeValue))
                if (payloadArraySize.error != null) return XyoResult(payloadArraySize.error ?: XyoError(
                        this.toString(),
                        "Unknown payloads size read error!")
                )
                val payloadArraySizeValue = payloadArraySize.value ?: return XyoResult(XyoError(
                        this.toString(),
                        "Unpacking payloads returned null!")
                )
                val payloadArrayResult = XyoSingleTypeArrayInt.createFromPacked(byteReader.read(currentOffset, payloadArraySizeValue))
                if (payloadArrayResult.error != null) return XyoResult(payloadArrayResult.error ?: XyoError(
                        this.toString(),
                        "Unknown payloads read error!")
                )
                payloadArray = payloadArrayResult.value as?  XyoSingleTypeArrayInt
                currentOffset += payloadArraySizeValue
            }

            if (type == 0x02.toByte() || type == 0x03.toByte()) {

                val signatureArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(currentOffset, shortArrayReadSizeValue))
                if (signatureArraySize.error != null) return XyoResult(signatureArraySize.error ?: XyoError(
                        this.toString(),
                        "Unknown signature size read error!")
                )
                val signatureArraySizeValue = signatureArraySize.value ?: return XyoResult(XyoError(
                        this.toString(),
                        "Unpacking signatures returned null!")
                )
                val signatureArrayResult = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(currentOffset, signatureArraySizeValue))
                if (signatureArrayResult.error != null) return XyoResult(signatureArraySize.error ?: XyoError(
                        this.toString(),
                        "Unknown signature read error!")
                )
                signatureArray = signatureArrayResult.value as? XyoSingleTypeArrayShort
                currentOffset += signatureArraySizeValue
            }

            val keySetArrayValue = keySetArray?.array ?: arrayOf()
            val payloadArrayValue = payloadArray?.array ?: arrayOf()
            val signatureArrayValue = signatureArray?.array ?: arrayOf()

            return XyoResult(XyoBoundWitnessTransfer(keySetArrayValue, payloadArrayValue, signatureArrayValue))
        }

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).int)
        }
    }
}