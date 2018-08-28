package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoKeySet(override var array: Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfBytesToGetSize

    override fun addElement(element: XyoObject, index: Int) {
        if (element.id[0] == 0x04.toByte()) {
            super.addElement(element, index)
        }
    }

    companion object : XyoArrayCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x02

        override val sizeOfBytesToGetSize: Int
            get() = 2

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).short.toInt()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoKeySet {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 2)
            val unpackedArrayObject = XyoKeySet(unpackedArray.array.toTypedArray())
            return unpackedArrayObject
        }
    }
}