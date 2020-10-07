package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * An interface schema to interpret a byteArray into an XyoObjectStructure (structures are part of the objectModel).
 */
@ExperimentalStdlibApi
interface XyoInterpret {
    fun getInstance (byteArray: ByteArray) : XyoObjectStructure
}