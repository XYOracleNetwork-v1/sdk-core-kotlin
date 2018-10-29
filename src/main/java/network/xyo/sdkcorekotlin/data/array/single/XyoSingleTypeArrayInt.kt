package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException

/**
 * An single type array with a 4 byte size.
 *
 * @major 0x01
 * @minor 0x03
 *
 * @param elementMajor The major type of the elements in the array.
 * @param elementMinor The minor type of the elements in the array.
 * @param array The in-memory array to start off the Xyo array with.
 */
open class XyoSingleTypeArrayInt(override val elementMajor : Byte,
                                 override val elementMinor : Byte,
                                 override var array: Array<XyoObject>) : XyoSingleTypeArrayBase() {

    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    companion object : XyoSingleTypeCreator() {
        override val minor: Byte = 0x03
        override val sizeOfBytesToGetSize: Int? = 4

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }

        override fun newInstance(majorType: Byte, minorType: Byte, array: Array<XyoObject>): XyoSingleTypeArrayBase {
            return XyoSingleTypeArrayInt(majorType, minorType, array)
        }
    }
}