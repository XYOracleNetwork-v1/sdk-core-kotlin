package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.exceptions.TypeExeception
import java.nio.ByteBuffer

class XyoStrongArray(major : Byte, minor : Byte) : XyoArrayBase() {
    private val mMajorType : Byte = major
    private val mMinorType : Byte = minor

    override val arraySize: ByteArray
        get() = ByteBuffer.allocate(4).putInt(size).array()

    override val typedId: ByteArray?
        get() = XyoRssi.id

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = sizeOfSize

    override fun addElement(element: XyoObject) {
        if (element.id[0] == mMajorType && element.id[1] == mMinorType) {
            super.addElement(element)
        } else {
            throw TypeExeception(mMajorType, mMinorType, element.id[0], element.id[1])
        }
    }

    override fun addElement(element: XyoObject, index: Int) {
        if (element.id[0] == mMajorType && element.id[1] == mMinorType) {
            super.addElement(element, index)
        } else {
            throw TypeExeception(mMajorType, mMinorType, element.id[0], element.id[1])
        }
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x01

        override val minor: Byte
            get() = 0x02

        override val defaultSize: Int?
            get() = null

        override val sizeOfSize: Int?
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val unpackedArray = XyoArrayUnpacker(byteArray, true, 4, 4)
            val unpackedArrayObject = XyoStrongArray(unpackedArray.majorType!!, unpackedArray.minorType!!)
            unpackedArrayObject.array = unpackedArray.array
            return unpackedArrayObject
        }
    }
}