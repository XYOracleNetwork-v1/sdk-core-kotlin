package network.xyo.sdkcorekotlin.data.heuristics.number.signed

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoNumberSigned

class XyoRssi (rssi : Int) : XyoNumberSigned() {
    override val number: Int = rssi

    override val size: XyoNumberTypes = XyoNumberTypes.BYTE

    override val sizeIdentifierSize: Int? = null

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x08

        override val minor: Byte
            get() = 0x01

        override fun readSize(byteArray: ByteArray): Int {
            return 1
        }

        override val sizeOfBytesToGetSize: Int
            get() = 0

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoRssi(byteArray[0].toInt())
        }
    }
}