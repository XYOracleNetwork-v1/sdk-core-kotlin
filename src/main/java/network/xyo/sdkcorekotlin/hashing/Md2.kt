package network.xyo.sdkcorekotlin.hashing

class Md2 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override val defaultSize: Int?
            get() = 16

        override val minor: Byte
            get() = 0x01

        override val standardDigestKey: String
            get() = "MD2"
    }
}