package network.xyo.sdkcorekotlin

import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

interface XyoFromSelf {
    fun getInstance (byteArray: ByteArray) : XyoBuff
}