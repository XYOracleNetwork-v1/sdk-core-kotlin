package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException

/**
 * An single type array with a 1 byte size.
 *
 * @major 0x01
 * @minor 0x01
 *
 * @param elementMajor The major type of the elements in the array.
 * @param elementMinor The minor type of the elements in the array.
 * @param array The in-memory array to start off the Xyo array with.
 */
open class XyoSingleTypeArrayByte(override val elementMajor : Byte,
                                  override val elementMinor : Byte,
                                  override var array: Array<XyoObject>) : XyoSingleTypeArrayBase() {

    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    companion object : XyoSingleTypeCreator() {
        override val minor: Byte = 0x01
        override val sizeOfBytesToGetSize: Int? = 1

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedByte(byteArray)
        }

        override fun newInstance(majorType: Byte, minorType: Byte, array: Array<XyoObject>): XyoSingleTypeArrayBase {
            return XyoSingleTypeArrayByte(majorType, minorType, array)
        }
    }
}