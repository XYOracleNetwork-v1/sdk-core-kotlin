package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoSingleTypeArrayShort(override val elementMajor : Byte,
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
            get() = 0x02

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).short.toInt()
        }

        override val sizeOfBytesToGetSize: Int
            get() = 2

        override fun createFromPacked(byteArray: ByteArray): XyoSingleTypeArrayInt {
            val unpackedArray = XyoArrayUnpacker(byteArray, true, 2)
            val unpackedArrayObject = XyoSingleTypeArrayInt(unpackedArray.majorType!!, unpackedArray.minorType!!, unpackedArray.array.toTypedArray())
            return unpackedArrayObject
        }
    }
}