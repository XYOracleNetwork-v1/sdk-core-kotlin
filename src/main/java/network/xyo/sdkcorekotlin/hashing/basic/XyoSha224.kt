package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * A Xyo Encoded Sha224 hash
 *
 * @major 0x03
 * @minor 0x04
 */
object XyoSha224 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String
        get() = "SHA224"

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.SHA224
}