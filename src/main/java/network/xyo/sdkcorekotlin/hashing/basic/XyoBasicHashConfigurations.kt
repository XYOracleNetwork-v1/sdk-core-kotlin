package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema


@ExperimentalUnsignedTypes
val XyoMd2 = XyoBasicHashBase.createHashType(XyoSchemas.MD2, "MD2")

@ExperimentalUnsignedTypes
val XyoMd5 = XyoBasicHashBase.createHashType(XyoSchemas.MD5, "MD5")

@ExperimentalUnsignedTypes
val XyoSha1 = XyoBasicHashBase.createHashType(XyoSchemas.SHA1, "SHA-1")

@ExperimentalUnsignedTypes
val XyoSha224 = XyoBasicHashBase.createHashType(XyoSchemas.SHA224, "SHA-224")

@ExperimentalUnsignedTypes
val XyoSha256 = XyoBasicHashBase.createHashType(XyoSchemas.SHA256, "SHA-256")

@ExperimentalUnsignedTypes
val XyoSha284 = XyoBasicHashBase.createHashType(XyoSchemas.SHA384, "SHA-384")

@ExperimentalUnsignedTypes
val XyoSha512 = XyoBasicHashBase.createHashType(XyoSchemas.SHA512, "SHA-512")