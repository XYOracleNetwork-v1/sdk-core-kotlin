package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * Schemas available
 */
@ExperimentalStdlibApi
object XyoSchemas {
    val ARRAY_TYPED = XyoObjectSchema(byteArrayOf(         0xB0.toByte(),        1.toByte()))
    val ARRAY_UNTYPED =  XyoObjectSchema(byteArrayOf(      0xA0.toByte(),        1.toByte()))
    val BW = XyoObjectSchema(byteArrayOf(                  0xA0.toByte(),        2.toByte()))
    val INDEX = XyoObjectSchema(byteArrayOf(               0x80.toByte(),        3.toByte()))
    val NEXT_PUBLIC_KEY = XyoObjectSchema(byteArrayOf(     0x80.toByte(),        4.toByte()))
    val BRIDGE_BLOCK_SET = XyoObjectSchema(byteArrayOf(    0xA0.toByte(),        5.toByte()))
    val BRIDGE_HASH_SET =  XyoObjectSchema(byteArrayOf(    0xB0.toByte(),        6.toByte()))
    val PAYMENT_KEY =  XyoObjectSchema(byteArrayOf(        0x00.toByte(),        7.toByte()))
    val PREVIOUS_HASH = XyoObjectSchema(byteArrayOf(       0xB0.toByte(),        8.toByte()))
    val EC_SIGNATURE = XyoObjectSchema(byteArrayOf(        0x80.toByte(),        9.toByte()))
    val RSA_SIGNATURE = XyoObjectSchema(byteArrayOf(       0x80.toByte(),        10.toByte()))
    val STUB_SIGNATURE = XyoObjectSchema(byteArrayOf(      0x80.toByte(),        11.toByte()))
    val EC_PUBLIC_KEY = XyoObjectSchema(byteArrayOf(       0x80.toByte(),        12.toByte()))
    val RSA_PUBLIC_KEY = XyoObjectSchema(byteArrayOf(      0x80.toByte(),        13.toByte()))
    val STUB_PUBLIC_KEY = XyoObjectSchema(byteArrayOf(     0x80.toByte(),        14.toByte()))
    val STUB_HASH = XyoObjectSchema(byteArrayOf(           0x80.toByte(),        15.toByte()))
    val SHA_256 =  XyoObjectSchema(byteArrayOf(            0x80.toByte(),        16.toByte()))
    val SHA_3 = XyoObjectSchema(byteArrayOf(               0x80.toByte(),        17.toByte()))
    val GPS = XyoObjectSchema(byteArrayOf(                 0xA0.toByte(),        18.toByte()))
    val RSSI = XyoObjectSchema(byteArrayOf(                0x80.toByte(),        19.toByte()))
    val UNIX_TIME = XyoObjectSchema(byteArrayOf(           0x80.toByte(),        20.toByte()))
    val FETTER = XyoObjectSchema(byteArrayOf(              0xA0.toByte(),        21.toByte()))
    val FETTER_SET =  XyoObjectSchema(byteArrayOf(         0xB0.toByte(),        22.toByte()))
    val WITNESS = XyoObjectSchema(byteArrayOf(             0xA0.toByte(),        23.toByte()))
    val WITNESS_SET = XyoObjectSchema(byteArrayOf(         0xB0.toByte(),        24.toByte()))
    val KEY_SET = XyoObjectSchema(byteArrayOf(             0xA0.toByte(),        25.toByte()))
    val SIGNATURE_SET = XyoObjectSchema(byteArrayOf(       0xA0.toByte(),        26.toByte()))
    val BW_FRAGMENT =  XyoObjectSchema(byteArrayOf(        0xA0.toByte(),        27.toByte()))
    val LAT =  XyoObjectSchema(byteArrayOf(                0x00.toByte(),        28.toByte()))
    val LNG =  XyoObjectSchema(byteArrayOf(                0x00.toByte(),        29.toByte()))
    val BLE_POWER_LVL = XyoObjectSchema(byteArrayOf(       0x80.toByte(),        30.toByte()))

    val EC_PRIVATE_KEY = XyoObjectSchema(byteArrayOf(      0x80.toByte(),        0xFF.toByte()))
    val RSA_PRIVATE_KEY = XyoObjectSchema(byteArrayOf(     0x80.toByte(),        0xFF.toByte()))
    val BLOB = XyoObjectSchema(byteArrayOf(                0x80.toByte(),        0xFF.toByte()))
}