package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import java.nio.ByteBuffer

abstract class XyoNumberUnsigned : XyoObject() {
    abstract val size : XyoNumberTypes
    abstract val number : Int

    override val data: ByteArray
        get() {
            when (size) {
                XyoNumberTypes.BYTE ->
                    return ByteBuffer.allocate(1).put(number.toByte()).array()

                XyoNumberTypes.SHORT ->
                    return ByteBuffer.allocate(2).putShort(number.toShort()).array()

                XyoNumberTypes.INT ->
                    return ByteBuffer.allocate(4).putInt(number).array()

                XyoNumberTypes.LONG ->
                    return ByteBuffer.allocate(8).putLong(number.toLong()).array()

                XyoNumberTypes.FLOAT ->
                    return ByteBuffer.allocate(4).putFloat(number.toFloat()).array()
                else -> {
                    return ByteBuffer.allocate(4).putInt(number).array()
                }
            }
        }
}