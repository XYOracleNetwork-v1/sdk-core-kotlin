package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * A Xyo Encoded Sha384 hash
 *
 * @major 0x03
 * @minor 0x06
 */
object XyoSha384 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String
        get() = "SHA384"

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.SHA384
}