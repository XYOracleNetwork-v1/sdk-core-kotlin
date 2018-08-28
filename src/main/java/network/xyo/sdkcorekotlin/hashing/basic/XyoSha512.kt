package network.xyo.sdkcorekotlin.hashing.basic

class XyoSha512 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override val minor: Byte
            get() = 0x07

        override fun readSize(byteArray: ByteArray): Int {
            return 64
        }

        override val standardDigestKey: String
            get() = "SHA-512"
    }
}