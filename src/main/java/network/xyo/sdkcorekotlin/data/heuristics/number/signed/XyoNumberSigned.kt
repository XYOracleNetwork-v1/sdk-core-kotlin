package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import java.nio.ByteBuffer

abstract class XyoNumberSigned : XyoObject() {
    abstract val size : XyoNumberTypes
    abstract val number : Int

    override val data: XyoResult<ByteArray>
        get() {
            when (size) {
                XyoNumberTypes.BYTE ->
                    return XyoResult(ByteBuffer.allocate(1).put(number.toByte()).array())

                XyoNumberTypes.SHORT ->
                    return XyoResult(ByteBuffer.allocate(2).putShort(number.toShort()).array())

                XyoNumberTypes.INT ->
                    return XyoResult(ByteBuffer.allocate(4).putInt(number).array())

                XyoNumberTypes.LONG ->
                    return XyoResult(ByteBuffer.allocate(8).putLong(number.toLong()).array())

                XyoNumberTypes.FLOAT ->
                    return XyoResult(ByteBuffer.allocate(4).putFloat(number.toFloat()).array())
                else -> {
                    return XyoResult(XyoError(this.toString(), "Not a valid size!"))
                }
            }
        }
}