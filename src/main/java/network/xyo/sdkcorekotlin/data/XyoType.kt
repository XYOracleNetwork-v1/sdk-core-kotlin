package network.xyo.sdkcorekotlin.data

abstract class XyoType {
    abstract val major : Byte
    abstract val minor : Byte

    val id : ByteArray
        get() = byteArrayOf(major, minor)
}