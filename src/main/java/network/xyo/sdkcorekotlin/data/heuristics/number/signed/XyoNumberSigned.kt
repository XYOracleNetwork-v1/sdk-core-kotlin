package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import java.nio.ByteBuffer

/**
 * A base class for encoding signed numbers.
 */
abstract class XyoNumberSigned : XyoObject() {
    /**
     * The type of number to encode the number to.
     */
    abstract val size : XyoNumberTypes

    /**
     * The number to encode.
     */
    abstract val number : Int

    override val objectInBytes: ByteArray
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
                    throw Exception("Not a valid size!")
                }
            }
        }
}