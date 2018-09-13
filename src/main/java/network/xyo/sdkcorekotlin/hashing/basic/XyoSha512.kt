package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Sha512 hash
 *
 * @major 0x03
 * @minor 0x07
 */
class XyoSha512 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val standardDigestKey: String = "SHA-512"
        override val minor: Byte = 0x07

        override fun readSize(byteArray: ByteArray): Int {
            return 64
        }
    }
}