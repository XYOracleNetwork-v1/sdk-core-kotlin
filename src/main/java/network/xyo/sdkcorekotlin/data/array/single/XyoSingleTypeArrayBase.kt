package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException

/**
 * A base class for single typed arrays.
 */
abstract class XyoSingleTypeArrayBase : XyoArrayBase() {
    /**
     * The type of the elements in the array major.
     */
    abstract val elementMajor : Byte

    /**
     * The type of the elements in the array minor.
     */
    abstract val elementMinor : Byte

    override fun addElement(element: XyoObject, index: Int) {
        val elementId = element.id
        if (elementId[0] == elementMajor && elementId[0] == elementMinor) {
            super.addElement(element, index)
        }
    }

    override val typedId: ByteArray?
        get() = byteArrayOf(elementMajor, elementMinor)

    abstract class XyoSingleTypeCreator : XyoArrayProvider() {
        abstract fun newInstance (majorType : Byte, minorType : Byte, array: Array<XyoObject>) : XyoSingleTypeArrayBase

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val unpackedArray = XyoArrayDecoder(byteArray, true, sizeOfBytesToGetSize ?: 0)
            val array = unpackedArray.array
            val majorType = unpackedArray.majorType ?: throw XyoCorruptDataException("No Major")
            val minorType = unpackedArray.minorType ?: throw XyoCorruptDataException("No Minor")
            return newInstance(majorType, minorType, array.toTypedArray())
        }
    }
}