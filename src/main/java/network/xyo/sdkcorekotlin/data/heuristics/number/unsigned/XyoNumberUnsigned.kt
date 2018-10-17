package network.xyo.sdkcorekotlin.data.heuristics.number.unsigned

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoNumberTypes

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
    abstract val number : Number


    override val objectInBytes: ByteArray
        get() {
            when (size) {
                XyoNumberTypes.BYTE -> return XyoUnsignedHelper.createUnsignedByte(number.toInt())

                XyoNumberTypes.SHORT -> return XyoUnsignedHelper.createUnsignedShort(number.toInt())

                XyoNumberTypes.INT -> return XyoUnsignedHelper.createUnsignedInt(number.toInt())

                XyoNumberTypes.LONG -> return XyoUnsignedHelper.createUnsignedLong(number.toLong())

                else -> {
                    throw Exception("Not a valid size!")
                }
            }
        }
}