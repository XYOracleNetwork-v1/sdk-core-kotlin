package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

interface XyoInterpret {
    fun getInstance (byteArray: ByteArray) : XyoObjectStructure
}