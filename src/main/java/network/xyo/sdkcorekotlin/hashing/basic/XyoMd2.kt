package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema


object XyoMd2 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String
        get() = "MD2"

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.MD2
}