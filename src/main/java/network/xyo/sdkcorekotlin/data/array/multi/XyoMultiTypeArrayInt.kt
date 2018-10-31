package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder

/**
 * An single type array with a 4 byte size.
 *
 * @major 0x01
 * @minor 0x06
 *
 * @param array The in-memory array to start off the Xyo array with.
 */
open class XyoMultiTypeArrayInt(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray = byteArrayOf(XyoMultiTypeArrayShort.major, minor)
    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    companion object : XyoMultiTypeArrayBaseCreator() {
        override val minor: Byte = 0x06
        override val sizeOfBytesToGetSize: Int? = 4

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }

        override fun newInstance(array: Array<XyoObject>): XyoMultiTypeArrayBase {
            return XyoMultiTypeArrayInt(array)
        }
    }
}

