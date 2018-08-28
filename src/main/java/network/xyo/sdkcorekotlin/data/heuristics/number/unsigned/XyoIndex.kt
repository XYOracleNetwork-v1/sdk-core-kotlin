package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import java.nio.ByteBuffer

class XyoIndex(override val number: Int) : XyoNumberUnsigned() {
    override val size: XyoNumberTypes
        get() = XyoNumberTypes.INT

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = null

    companion object : XyoObjectCreator() {
        override fun readSize(byteArray: ByteArray): Int {
            return 4
        }

        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x04

        override val sizeOfBytesToGetSize: Int
            get() = 0

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoIndex(ByteBuffer.wrap(byteArray).int)
        }
    }
}