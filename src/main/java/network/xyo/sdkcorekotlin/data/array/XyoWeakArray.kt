package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import java.nio.ByteBuffer

class XyoWeakArray : XyoArrayBase() {
    override val arraySize: ByteArray
        get() = ByteBuffer.allocate(4).putInt(size).array()

    override val typedId: ByteArray?
        get() = null

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfSize

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x01

        override val minor: Byte
            get() = 0x03

        override val defaultSize: Int?
            get() = null

        override val sizeOfSize: Int?
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 4, 4)
            val unpackedArrayObject = XyoWeakArray()
            unpackedArrayObject.array = unpackedArray.array
            return unpackedArrayObject
        }
    }
}