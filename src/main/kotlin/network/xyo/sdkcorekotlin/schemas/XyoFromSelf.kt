package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

interface XyoFromSelf {
    fun getInstance (byteArray: ByteArray) : XyoBuff
}