package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema


object XyoSha1 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA1
    override val standardDigestKey: String = "SHA1"
}
