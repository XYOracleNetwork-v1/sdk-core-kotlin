package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Sha1 hash
 *
 * @major 0x03
 * @minor 0x03
 */
class XyoSha1 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x03
        override val standardDigestKey: String = "SHA-1"

        override fun readSize(byteArray: ByteArray): Int {
            return 20
        }
    }
}