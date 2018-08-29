package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import java.nio.ByteBuffer

class XyoIndex(override val number: Int) : XyoNumberUnsigned() {
    override val size: XyoNumberTypes
        get() = XyoNumberTypes.INT

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult<Int?>(null)

    companion object : XyoObjectCreator() {
        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(4)
        }

        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x04

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(0)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            return XyoResult(XyoIndex(ByteBuffer.wrap(byteArray).int))
        }
    }
}