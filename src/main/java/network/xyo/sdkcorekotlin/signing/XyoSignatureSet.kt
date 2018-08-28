package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayBase
import java.nio.ByteBuffer

open class XyoSignatureSet(override var array: Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfBytesToGetSize

    override fun addElement(element: XyoObject, index: Int) {
        if (element.id[0] == 0x05.toByte()) {
            super.addElement(element, index)
        }
    }

    companion object : XyoArrayCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x03

        override val sizeOfBytesToGetSize: Int
            get() = 2

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).short.toInt()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoSignatureSet {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 2)
            val unpackedArrayObject = XyoSignatureSet(unpackedArray.array.toTypedArray())
            return unpackedArrayObject
        }
    }
}