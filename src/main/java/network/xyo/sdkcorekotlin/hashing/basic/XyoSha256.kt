package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * A Xyo Encoded Sha256 hash
 *
 * @major 0x03
 * @minor 0x05
 */
object XyoSha256 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA256
    override val standardDigestKey: String = "SHA-256"
}