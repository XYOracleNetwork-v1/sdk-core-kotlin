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

        override val defaultSize: Int?
            get() = 1

        override val sizeOfSize: Int?
            get() = null

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            if (byteArray.size != 3)
                throw Exception()
            return XyoRssi(byteArray[2].toInt())
        }
    }
}