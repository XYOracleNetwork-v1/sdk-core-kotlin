package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

object XyoMd2 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.MD2
    override val standardDigestKey: String = "MD2"
}

object XyoMd5 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.MD5
    override val standardDigestKey: String = "MD5"
}

object XyoSha1 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA1
    override val standardDigestKey: String = "SHA1"
}

object XyoSha224 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA224
    override val standardDigestKey: String = "SHA-224"
}

object XyoSha384 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA384
    override val standardDigestKey: String = "SHA-384"
}

object XyoSha256 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA256
    override val standardDigestKey: String = "SHA-256"
}

object XyoSha512 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.SHA512
    override val standardDigestKey: String = "SHA-512"
}