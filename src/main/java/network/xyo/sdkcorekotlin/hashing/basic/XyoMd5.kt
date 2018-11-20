package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema


object XyoMd5 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.MD5
    override val standardDigestKey: String = "MD5"
}