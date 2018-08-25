package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker

class XyoMultiTypeArrayInt : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x06

        override val sizeOfSize: Int?
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoMultiTypeArrayInt {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 4)
            val unpackedArrayObject = XyoMultiTypeArrayInt()
            unpackedArrayObject.array = unpackedArray.array
            return unpackedArrayObject
        }
    }
}