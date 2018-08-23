package network.xyo.sdkcorekotlin.hashing


class Sha1 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override val defaultSize: Int?
            get() = 20

        override val minor: Byte
            get() = 0x03

        override val standardDigestKey: String
            get() = "SHA-1"
    }
}