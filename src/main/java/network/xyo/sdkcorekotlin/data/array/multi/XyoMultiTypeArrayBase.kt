package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.array.XyoArrayBase

/**
 * The base class for multi typed arrays.
 */
abstract class XyoMultiTypeArrayBase : XyoArrayBase() {
    override val typedId: ByteArray?
        get() = null
}