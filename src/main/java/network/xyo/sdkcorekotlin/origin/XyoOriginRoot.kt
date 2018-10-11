package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import java.util.*

open class XyoOriginRoot : XyoObject() {
    open val publicKeys = LinkedList<XyoObject>()
    open val hashes = LinkedList<XyoObject>()

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val objectInBytes: ByteArray
        get() = encode()

    override val sizeIdentifierSize: Int?
        get() = 4

    private fun encode () : ByteArray {
        val publicKeysArray = XyoMultiTypeArrayInt(publicKeys.toTypedArray()).untyped
        val publicKeysArraySize = XyoUnsignedHelper.createUnsignedInt(publicKeysArray.size + 4)
        val hashArray = XyoMultiTypeArrayInt(hashes.toTypedArray()).untyped
        val hashArraySize = XyoUnsignedHelper.createUnsignedInt(hashArray.size + 4)

        val merger = XyoByteArraySetter(4)
        merger.add(publicKeysArraySize, 0)
        merger.add(publicKeysArray, 1)
        merger.add(hashArray, 2)
        merger.add(hashArraySize, 3)

        return merger.merge()
    }

    companion object : XyoObjectProvider() {
        override val major: Byte
            get() = 0x0d

        override val minor: Byte
            get() = 0x0d

        override val sizeOfBytesToGetSize: Int?
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val totalSize = XyoUnsignedHelper.readUnsignedInt(reader.read(0, 4)) - 4
            println("totalSize $totalSize")
            val publicKeysArraySize = XyoUnsignedHelper.readUnsignedInt(reader.read(4, 4))
            println("publicKeysArraySize $publicKeysArraySize")
            val hashArraySize = XyoUnsignedHelper.readUnsignedInt(reader.read(4 + publicKeysArraySize, 4))
            println("hashArraySize $hashArraySize")

            val publicKeyArray = XyoMultiTypeArrayInt.createFromPacked(reader.read(8, publicKeysArraySize - 4)) as XyoMultiTypeArrayInt
            println("publicKeyArray")
            val hashArray = XyoMultiTypeArrayInt.createFromPacked(reader.read(4 + publicKeysArraySize, hashArraySize)) as XyoMultiTypeArrayInt
            println("hashArray")

            return object : XyoOriginRoot () {
                override val hashes: LinkedList<XyoObject> = LinkedList(hashArray.array.asList())
                override val publicKeys: LinkedList<XyoObject> = LinkedList(publicKeyArray.array.asList())
            }
        }

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }
    }
}