package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker

open class XyoSingleTypeArrayByte(override val elementMajor : Byte,
                                  override val elementMinor : Byte,
                                  override var array: Array<XyoObject>) : XyoSingleTypeArrayBase() {

    override val typedId: ByteArray?
        get() = byteArrayOf(elementMajor, elementMinor)

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x01

        override val sizeOfBytesToGetSize: Int
            get() = 1

        override fun readSize(byteArray: ByteArray): Int {
            return byteArray[0].toInt()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoSingleTypeArrayByte {
            val unpackedArray = XyoArrayUnpacker(byteArray, true, 1)
            val unpackedArrayObject = XyoSingleTypeArrayByte(unpackedArray.majorType!!, unpackedArray.minorType!!, unpackedArray.array.toTypedArray())
            return unpackedArrayObject
        }
    }
}