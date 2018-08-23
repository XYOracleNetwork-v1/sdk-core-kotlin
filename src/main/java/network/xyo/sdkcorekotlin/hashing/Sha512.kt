package network.xyo.sdkcorekotlin.hashing

class Sha512 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override val defaultSize: Int?
            get() = 64

        override val minor: Byte
            get() = 0x07

        override val standardDigestKey: String
            get() = "SHA-512"
    }
}