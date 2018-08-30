package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.array.XyoArrayBase

abstract class XyoMultiTypeArrayBase : XyoArrayBase() {
    override val typedId: ByteArray?
        get() = null
}