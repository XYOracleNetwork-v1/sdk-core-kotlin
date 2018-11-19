package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * A Xyo Encoded Md5 hash
 *
 * @major 0x03
 * @minor 0x02
 */
object XyoMd5 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String
        get() = "MD5"

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.MD5
}