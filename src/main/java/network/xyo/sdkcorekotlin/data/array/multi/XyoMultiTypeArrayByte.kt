package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder

/**
 * An single type array with a 1 byte size.
 *
 * @major 0x01
 * @minor 0x04
 *
 * @param array The in-memory array to start off the Xyo array with.
 */
open class XyoMultiTypeArrayByte(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray = byteArrayOf(XyoMultiTypeArrayShort.major, minor)
    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    companion object : XyoArrayProvider() {
        override val minor: Byte = 0x04
        override val sizeOfBytesToGetSize: Int? = 1

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedByte(byteArray)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val unpackedArray = XyoArrayDecoder(byteArray, false, 1)
            val array = unpackedArray.array
            return XyoMultiTypeArrayByte(array.toTypedArray())
        }
    }
}