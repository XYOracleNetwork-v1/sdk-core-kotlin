package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

@ExperimentalUnsignedTypes
object XyoSchemas {
    val BW = XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x02))
    val ARRAY_TYPED = XyoObjectSchema.createFromHeader(byteArrayOf(0xB0.toByte(), 0x01))
    val ARRAY_UNTYPED =  XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x01))
    val SIGNATURE_SET =  XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x03))
    val KEY_SET =  XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x04))
    val MD2 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x05))
    val MD5 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x06))
    val SHA1= XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x07))
    val SHA224 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x08))
    val SHA256 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x09))
    val SHA384 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x0a))
    val SHA512 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x0b))
    val SHA3 = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x0c))
    val PREVIOUS_HASH = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x0d))
    val BRIDGE_BLOCK_SET = XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x0e))
    val BRIDGE_HASH_SET =  XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x0f))
    val UNIX_TIME = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x10))
    val NEXT_PUBLIC_KEY = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x11))
    val EC_PUBLIC_KEY = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x12))
    val EC_PRIVATE_KEY = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x13))
    val EC_SIGNATURE = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x14))
    val RSA_PUBLIC_KEY = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x16))
    val RSA_PRIVATE_KEY = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x15))
    val RSA_SIGNATURE = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x17))
    val PAYLOAD = XyoObjectSchema.createFromHeader(byteArrayOf(0xA0.toByte(), 0x18))
    val INDEX = XyoObjectSchema.createFromHeader(byteArrayOf(0x80.toByte(), 0x19))
}