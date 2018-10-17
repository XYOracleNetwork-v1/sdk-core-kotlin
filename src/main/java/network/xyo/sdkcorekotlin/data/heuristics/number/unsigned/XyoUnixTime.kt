package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes

/**
 * A unix time heuristic.
 *
 * @param number The current unix time
 * @major 0x0d
 * @minor 0x0f
 */
class XyoUnixTime(override val number: Long) : XyoNumberUnsigned() {
    override val size: XyoNumberTypes = XyoNumberTypes.INT
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = null

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x0d
        override val minor: Byte = 0x0f
        override val sizeOfBytesToGetSize: Int? = 0

        override fun readSize(byteArray: ByteArray): Int {
            return 8
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoUnixTime(XyoUnsignedHelper.readUnsignedLong(byteArray))
        }
    }
}