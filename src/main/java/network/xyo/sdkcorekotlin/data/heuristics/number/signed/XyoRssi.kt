package network.xyo.sdkcorekotlin.data.heuristics.number.signed

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoNumberSigned

/**
 * The Xyo Rssi heuristic.
 *
 * @major 0x08
 * @minor 0x01
 *
 * @param rssi The rssi value to be encoded.
 */
class XyoRssi (rssi : Int) : XyoNumberSigned() {
    override val number: Int = rssi
    override val size: XyoNumberTypes = XyoNumberTypes.BYTE
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult<Int?>(null)

    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x08
        override val minor: Byte = 0x01
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(0)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(1)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            return XyoResult(XyoRssi(byteArray[0].toInt()))
        }
    }
}