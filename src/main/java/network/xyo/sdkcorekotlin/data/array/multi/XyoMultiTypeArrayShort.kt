package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker

class XyoMultiTypeArrayShort : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x05

        override val sizeOfSize: Int?
            get() = 2

        override fun createFromPacked(byteArray: ByteArray): XyoArrayBase {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 2)
            val unpackedArrayObject = XyoMultiTypeArrayByte()
            unpackedArrayObject.array = unpackedArray.array
            return unpackedArrayObject
        }
    }
}