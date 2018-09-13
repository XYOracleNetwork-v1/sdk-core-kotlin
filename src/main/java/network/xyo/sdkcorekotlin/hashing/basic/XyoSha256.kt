package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Sha256 hash
 *
 * @major 0x03
 * @minor 0x05
 */
class XyoSha256 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x05
        override val standardDigestKey: String = "SHA-256"

        override fun readSize(byteArray: ByteArray): Int {
            return 32
        }
    }
}
