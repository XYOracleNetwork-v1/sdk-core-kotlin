package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes

/**
 * A unix time heuristic.
 *
 * @param unixTime Pass null for current unix time on get and
 * @major 0x0d
 * @minor 0x0f
 */
open class XyoUnixTime(private val unixTime : Long) : XyoNumberUnsigned() {
    override val size: XyoNumberTypes = XyoNumberTypes.LONG
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = null
    override val number: Number = unixTime

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