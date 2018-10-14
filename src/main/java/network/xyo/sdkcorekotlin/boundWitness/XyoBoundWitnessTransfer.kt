package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet

/**
 * A object to use when transferring data when making a bound witness with another party.
 *
 * @major 0x02
 * @minor 0x0a
 *
 * @param keysToSend The keys to send to the other party.
 * @param payloadsToSend The payloads to send to the other party.
 * @param signatureToSend The signaturePacking to send to the other party.
 */
class XyoBoundWitnessTransfer(val keysToSend : Array<XyoObject>,
                              val payloadsToSend : Array<XyoObject>,
                              val signatureToSend : Array<XyoObject>) : XyoObject() {

    override val objectInBytes: ByteArray = makeWithEverything()
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = 4

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

    private fun makeWithEverything() : ByteArray {
        when (stage) {
            0x01.toByte() -> return makeWithJustData()
            0x02.toByte() -> return makeWithDataAndSignatures()
            0x03.toByte() -> return makeWithJustSignatures()
        }
        throw Exception("Invalid stage!")
    }

    private fun makeWithJustSignatures () : ByteArray {
        val setter = XyoByteArraySetter(2)
        setter.add(byteArrayOf(0x03), 0)
        setter.add(XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped, 1)
        return setter.merge()
    }

    private fun makeWithJustData () : ByteArray {
        val setter = XyoByteArraySetter(3)
        setter.add(byteArrayOf(0x01), 0)
        setter.add(XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped, 1)
        setter.add(XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped, 2)
        return setter.merge()
    }

    private fun makeWithDataAndSignatures () : ByteArray {
        val setter = XyoByteArraySetter(4)
        setter.add(byteArrayOf(0x02), 0)
        setter.add(XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped, 1)
        setter.add(XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped, 2)
        setter.add(XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped, 3)
        return setter.merge()
    }

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x0a
        override val sizeOfBytesToGetSize: Int? = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val stage = byteArray[4]
            return unpackEverything(byteArray, 5, stage)
        }

        private fun unpackEverything(bytes: ByteArray, globalOffset : Int, type: Byte) : XyoObject {
            val byteReader = XyoByteArrayReader(bytes)
            var currentOffset = globalOffset

            var keySetArray : XyoSingleTypeArrayShort? = null
            var payloadArray : XyoSingleTypeArrayInt? = null
            var signatureArray :  XyoSingleTypeArrayShort? = null

            val shortArrayReadSize = XyoSingleTypeArrayShort.sizeOfBytesToGetSize ?: 0
            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize ?: 0


            if (type == 0x01.toByte() || type == 0x02.toByte()) {
                val keySetArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(currentOffset, shortArrayReadSize))
                val keySetArrayResult = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(currentOffset, keySetArraySize))
                keySetArray = keySetArrayResult as? XyoSingleTypeArrayShort
                currentOffset += keySetArraySize

                val payloadArraySize  = XyoSingleTypeArrayInt.readSize(byteReader.read(currentOffset, intArrayReadSize))
                val payloadArrayResult = XyoSingleTypeArrayInt.createFromPacked(byteReader.read(currentOffset, payloadArraySize))
                payloadArray = payloadArrayResult as?  XyoSingleTypeArrayInt
                currentOffset += payloadArraySize
            }

            if (type == 0x02.toByte() || type == 0x03.toByte()) {
                val signatureArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(currentOffset, shortArrayReadSize))
                val signatureArrayResult = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(currentOffset, signatureArraySize))
                signatureArray = signatureArrayResult as? XyoSingleTypeArrayShort
                currentOffset += signatureArraySize
            }

            val keySetArrayValue = keySetArray?.array ?: arrayOf()
            val payloadArrayValue = payloadArray?.array ?: arrayOf()
            val signatureArrayValue = signatureArray?.array ?: arrayOf()

            return XyoBoundWitnessTransfer(keySetArrayValue, payloadArrayValue, signatureArrayValue)
        }

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }
    }
}