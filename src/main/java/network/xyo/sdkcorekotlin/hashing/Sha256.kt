package network.xyo.sdkcorekotlin.hashing

class Sha256 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override val defaultSize: Int?
            get() = 32

        override val minor: Byte
            get() = 0x05

        override val standardDigestKey: String
            get() = "SHA-256"
    }
}