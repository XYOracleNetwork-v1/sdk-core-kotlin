package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema


val XyoMd2 = XyoBasicHashBase.createHashType(XyoSchemas.MD2, "MD2")

val XyoMd5 = XyoBasicHashBase.createHashType(XyoSchemas.MD5, "MD5")

val XyoSha1 = XyoBasicHashBase.createHashType(XyoSchemas.SHA1, "SHA-1")

val XyoSha224 = XyoBasicHashBase.createHashType(XyoSchemas.SHA224, "SHA-224")

val XyoSha256 = XyoBasicHashBase.createHashType(XyoSchemas.SHA256, "SHA-256")

val XyoSha284 = XyoBasicHashBase.createHashType(XyoSchemas.SHA384, "SHA-384")

val XyoSha512 = XyoBasicHashBase.createHashType(XyoSchemas.SHA512, "SHA-512")