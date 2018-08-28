package network.xyo.sdkcorekotlin.hashing.basic

class XyoMd5 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseCreator() {
        override fun readSize(byteArray: ByteArray): Int {
            return 16
        }

        override val minor: Byte
            get() = 0x02

        override val standardDigestKey: String
            get() = "MD5"
    }
}