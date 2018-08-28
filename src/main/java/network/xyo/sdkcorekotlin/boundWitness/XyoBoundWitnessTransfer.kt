package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoSignatureSet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import java.nio.ByteBuffer

class XyoBoundWitnessTransfer(val keysToSend : Array<XyoObject>,
                              val payloadsToSend : Array<XyoObject>,
                              val signatureToSend : Array<XyoObject>) : XyoObject() {

    override val data: ByteArray
        get() = makeEncoded()

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = 4

    private val stage : Byte
        get() {
            if (keysToSend.isNotEmpty() && payloadsToSend.isNotEmpty() && signatureToSend.isEmpty()) {
                return 0x01
            } else if (keysToSend.isNotEmpty() && payloadsToSend.isNotEmpty() && signatureToSend.isNotEmpty()) {
                return 0x02
            } else if (keysToSend.isEmpty() && payloadsToSend.isEmpty() && signatureToSend.isNotEmpty()) {
                return 0x03
            } else {
                throw Exception()
            }
        }

    private fun makeEncoded () : ByteArray {
        if (stage == 0x01.toByte() && keysToSend.isNotEmpty() && payloadsToSend.isNotEmpty()) {

            val setter = XyoByteArraySetter(3)
            setter.add(byteArrayOf(stage), 0)
            setter.add(XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped, 1)
            setter.add(XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped, 2)
            return setter.merge()

        } else if (stage == 0x02.toByte() && keysToSend.isNotEmpty() && payloadsToSend.isNotEmpty() && signatureToSend.isNotEmpty()) {

            val setter = XyoByteArraySetter(4)
            setter.add(byteArrayOf(stage), 0)
            setter.add(XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, keysToSend).untyped, 1)
            setter.add(XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloadsToSend).untyped, 2)
            setter.add(XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped, 3)
            return setter.merge()

        } else if (stage == 0x03.toByte() && signatureToSend.isNotEmpty()) {

            val setter = XyoByteArraySetter(2)
            setter.add(byteArrayOf(stage), 0)
            setter.add(XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatureToSend).untyped, 2)
            return setter.merge()

        }
        throw Exception()
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x08

        override val sizeOfBytesToGetSize: Int
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoBoundWitnessTransfer {
            val stage = byteArray[0]
            val byteReader = XyoByteArrayReader(byteArray)
            val shortArrayReadSize = XyoSingleTypeArrayShort.sizeOfBytesToGetSize
            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize

            if (stage == 0x01.toByte()) {

                val keySetArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(1, shortArrayReadSize))
                val keySetArray = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(1, keySetArraySize))
                val payloadArraySize = XyoSingleTypeArrayInt.readSize(byteReader.read(keySetArraySize + 1, intArrayReadSize))
                val payloadArray = XyoSingleTypeArrayInt.createFromPacked(byteReader.read(keySetArraySize + 1, payloadArraySize))
                return XyoBoundWitnessTransfer(keySetArray.array, payloadArray.array, arrayOf())

            } else if (stage == 0x02.toByte()) {

                val keySetArraySize  = XyoSingleTypeArrayShort.readSize(byteReader.read(1, shortArrayReadSize))
                val keySetArray = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(1, keySetArraySize))
                val payloadArraySize = XyoSingleTypeArrayInt.readSize(byteReader.read(keySetArraySize + 1, intArrayReadSize))
                val payloadArray = XyoSingleTypeArrayInt.createFromPacked(byteReader.read(keySetArraySize + 1, payloadArraySize))
                val signatureArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(keySetArraySize + payloadArraySize + 1, shortArrayReadSize))
                val signatureArray = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(keySetArraySize + payloadArraySize + 1, signatureArraySize))
                return XyoBoundWitnessTransfer(keySetArray.array, payloadArray.array, signatureArray.array)

            } else if (stage == 0x03.toByte()) {

                val signatureArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(1, shortArrayReadSize))
                val signatureArray = XyoSingleTypeArrayShort.createFromPacked(byteReader.read(1, signatureArraySize))
                return XyoBoundWitnessTransfer(arrayOf(), arrayOf(), signatureArray.array)

            }
            throw Exception()
        }

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).int
        }
    }
}