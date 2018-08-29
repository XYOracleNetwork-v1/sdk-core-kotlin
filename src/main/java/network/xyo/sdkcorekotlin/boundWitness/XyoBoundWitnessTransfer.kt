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

    override val data: XyoResult<ByteArray>
        get() = makeEncoded()

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult(4)

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
        val encodedKeySetsValue = encodedKeySets.value ?: return XyoResult(XyoError(""))
        if (encodedKeySets.error != null) return XyoResult(XyoError(""))
        setter.add(encodedKeySetsValue, 1)

        val encodedPayloads = XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped
        val encodedPayloadsValue = encodedPayloads.value ?: return XyoResult(XyoError(""))
        if (encodedPayloads.error != null) return XyoResult(XyoError(""))
        setter.add(encodedPayloadsValue, 2)

        return XyoResult(setter.merge())
    }

    private fun makeWithEverything() : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(4)
        setter.add(byteArrayOf(stage), 0)

        val encodedKeySets = XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped
        val encodedKeySetsValue = encodedKeySets.value ?: return XyoResult(XyoError(""))
        if (encodedKeySets.error != null) return XyoResult(XyoError(""))
        setter.add(encodedKeySetsValue, 1)

        val encodedPayloads = XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped
        val encodedPayloadsValue = encodedPayloads.value ?: return XyoResult(XyoError(""))
        if (encodedPayloads.error != null) return XyoResult(XyoError(""))
        setter.add(encodedPayloadsValue, 2)

        val encodedSignatures = XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped
        val encodedSignaturesValue = encodedSignatures.value ?: return XyoResult(XyoError(""))
        if (encodedSignatures.error != null) return XyoResult(XyoError(""))
        setter.add(encodedSignaturesValue, 3)

        return XyoResult(setter.merge())
    }

    private fun makeWithSignatures() : XyoResult<ByteArray> {
        val setter = XyoByteArraySetter(2)
        setter.add(byteArrayOf(stage), 0)

        val encodedSignatures = XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped
        val encodedSignaturesValue = encodedSignatures.value ?:  return XyoResult(XyoError(""))
        if (encodedSignatures.error != null) return XyoResult(XyoError(""))
        setter.add(encodedSignaturesValue, 3)

        return XyoResult(setter.merge())
    }

    private fun makeEncoded () : XyoResult<ByteArray> {
        when (stage) {
            0x01.toByte() -> return makeWithKeysAndPayload()
            0x02.toByte() -> return makeWithEverything()
            0x03.toByte() -> return makeWithSignatures()
        }
        return XyoResult(XyoError(""))
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x08

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(4)

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
            if (shortArrayReadSize.error != null) return XyoResult(XyoError(""))
            val shortArrayReadSizeValue = shortArrayReadSize.value ?: return XyoResult(XyoError(""))

            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize
            if (intArrayReadSize.error != null) return XyoResult(XyoError(""))
            val intArrayReadSizeValue = intArrayReadSize.value ?: return XyoResult(XyoError(""))

            if (type == 0x01.toByte() || type == 0x02.toByte()) {
                val keySetArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(currentOffset, shortArrayReadSizeValue))
                if (keySetArraySize.error != null) return XyoResult(XyoError(""))
                val keySetArraySizeValue = keySetArraySize.value ?: return XyoResult(XyoError(""))
                val keySetArrayResult = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(currentOffset, keySetArraySizeValue))
                if (keySetArrayResult.error != null) return XyoResult(XyoError(""))
                keySetArray = keySetArrayResult.value as? XyoSingleTypeArrayShort
                currentOffset += keySetArraySizeValue

                val payloadArraySize  = XyoSingleTypeArrayInt.readSize(byteReader.read(currentOffset, intArrayReadSizeValue))
                if (payloadArraySize.error != null) return XyoResult(XyoError(""))
                val payloadArraySizeValue = payloadArraySize.value ?: return XyoResult(XyoError(""))
                val payloadArrayResult = XyoSingleTypeArrayInt.createFromPacked(byteReader.read(currentOffset, payloadArraySizeValue))
                if (payloadArrayResult.error != null) return XyoResult(XyoError(""))
                payloadArray = payloadArrayResult.value as?  XyoSingleTypeArrayInt
                currentOffset += payloadArraySizeValue
            }

            if (type == 0x02.toByte() || type == 0x03.toByte()) {
                val signatureArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(currentOffset, shortArrayReadSizeValue))
                if (signatureArraySize.error != null) return XyoResult(XyoError(""))
                val signatureArraySizeValue = signatureArraySize.value ?: return XyoResult(XyoError(""))
                val signatureArrayResult = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(currentOffset, signatureArraySizeValue))
                if (signatureArrayResult.error != null) return XyoResult(XyoError(""))
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

        fun bytesToString(bytes: ByteArray?): String {
            val sb = StringBuilder()
            val it = bytes!!.iterator()
            sb.append("0x")
            while (it.hasNext()) {
                sb.append(String.format("%02X ", it.next()))
            }

            return sb.toString()
        }
    }
}