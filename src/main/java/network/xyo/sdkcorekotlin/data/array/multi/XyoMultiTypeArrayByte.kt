package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoMultiTypeArrayByte(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x04

        override val sizeOfBytesToGetSize: Int
            get() = 1

        override fun readSize(byteArray: ByteArray): Int {
            return byteArray[0].toInt()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoArrayBase {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 1)
            val unpackedArrayObject = XyoMultiTypeArrayByte(unpackedArray.array.toTypedArray())
            return unpackedArrayObject
        }
    }
}