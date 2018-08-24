package network.xyo.sdkcorekotlin.hashing.basic

class XyoSha384 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override val defaultSize: Int?
            get() = 48

        override val minor: Byte
            get() = 0x06

        override val standardDigestKey: String
            get() = "SHA-384"
    }
}