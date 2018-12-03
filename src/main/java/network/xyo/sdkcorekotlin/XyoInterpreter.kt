package network.xyo.sdkcorekotlin

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

interface XyoInterpreter {
    val self : ByteArray
    val schema : XyoObjectSchema
}