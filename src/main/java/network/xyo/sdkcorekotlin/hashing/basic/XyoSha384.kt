package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema


object XyoSha384 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA384
    override val standardDigestKey: String = "SHA-384"
}