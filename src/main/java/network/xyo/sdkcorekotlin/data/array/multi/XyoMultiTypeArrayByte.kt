package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker

class XyoMultiTypeArrayByte : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x04

        override val sizeOfSize: Int?
            get() = 1

        override fun createFromPacked(byteArray: ByteArray): XyoArrayBase {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 1)
            val unpackedArrayObject = XyoMultiTypeArrayByte()
            unpackedArrayObject.array = unpackedArray.array
            return unpackedArrayObject
        }
    }
}