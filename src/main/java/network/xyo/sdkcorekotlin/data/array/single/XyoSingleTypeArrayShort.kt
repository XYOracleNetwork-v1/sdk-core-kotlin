package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker

class XyoSingleTypeArrayShort(override val elementMajor : Byte,
                              override val elementMinor : Byte) : XyoSingleTypeArrayBase() {

    override val typedId: ByteArray?
        get() = byteArrayOf(elementMajor, elementMinor)

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x02

        override val sizeOfSize: Int?
            get() = 2

        override fun createFromPacked(byteArray: ByteArray): XyoSingleTypeArrayInt {
            val unpackedArray = XyoArrayUnpacker(byteArray, true, 2)
            val unpackedArrayObject = XyoSingleTypeArrayInt(unpackedArray.majorType!!, unpackedArray.minorType!!)
            unpackedArrayObject.array = unpackedArray.array
            return unpackedArrayObject
        }
    }
}