package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

interface XyoInterpret {
    fun getInstance (byteArray: ByteArray) : XyoBuff
}