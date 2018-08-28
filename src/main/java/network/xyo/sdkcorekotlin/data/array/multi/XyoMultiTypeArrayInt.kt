package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoMultiTypeArrayInt(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x06

        override val sizeOfBytesToGetSize: Int
            get() = 4

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).int
        }

        override fun createFromPacked(byteArray: ByteArray): XyoMultiTypeArrayInt {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 4)
            val unpackedArrayObject = XyoMultiTypeArrayInt(unpackedArray.array.toTypedArray())
            return unpackedArrayObject
        }
    }
}