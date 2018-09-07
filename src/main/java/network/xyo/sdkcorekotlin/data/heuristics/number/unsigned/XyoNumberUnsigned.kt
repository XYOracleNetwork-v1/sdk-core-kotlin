package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes
import java.nio.ByteBuffer

/**
 * A base class for encoding unsigned numbers.
 */
abstract class XyoNumberUnsigned : XyoObject() {
    /**
     * The type of number to encode the number to.
     */
    abstract val size : XyoNumberTypes

    /**
     * The number to encode.
     */
    abstract val number : Int

    override val objectInBytes: XyoResult<ByteArray>
        get() {
            when (size) {
                XyoNumberTypes.BYTE -> return XyoResult(XyoUnsignedHelper.createUnsignedByte(number))

                XyoNumberTypes.SHORT -> return XyoResult(XyoUnsignedHelper.createUnsignedShort(number))

                XyoNumberTypes.INT -> return XyoResult(XyoUnsignedHelper.createUnsignedInt(number))

                else -> {
                    return XyoResult(XyoError(this.toString(), "Not a valid size!"))
                }
            }
        }
}