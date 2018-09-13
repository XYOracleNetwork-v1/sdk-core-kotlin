package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Sha384 hash
 *
 * @major 0x03
 * @minor 0x06
 */
class XyoSha384 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x06
        override val standardDigestKey: String = "SHA-384"

        override fun readSize(byteArray: ByteArray): Int {
            return 48
        }
    }
}