package network.xyo.sdkcorekotlin.hashing.basic


class XyoSha224 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override fun readSize(byteArray: ByteArray): Int {
            return 20
        }

        override val minor: Byte
            get() = 0x04

        override val standardDigestKey: String
            get() = "SHA-224"
    }
}