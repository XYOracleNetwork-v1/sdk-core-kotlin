package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject

abstract class XyoSignature : XyoObject() {
    abstract val encodedSignature : ByteArray
}