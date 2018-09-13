package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Sha224 hash
 *
 * @major 0x03
 * @minor 0x04
 */
class XyoSha224 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x04
        override val standardDigestKey: String = "SHA-224"
        override fun readSize(byteArray: ByteArray): Int {
            return 20
        }
    }
}