package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Md5 hash
 *
 * @major 0x03
 * @minor 0x02
 */
class XyoMd5 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x02
        override val standardDigestKey: String = "MD5"

        override fun readSize(byteArray: ByteArray): Int {
            return 16
        }
    }
}