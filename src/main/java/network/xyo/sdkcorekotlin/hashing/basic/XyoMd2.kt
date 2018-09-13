package network.xyo.sdkcorekotlin.hashing.basic

/**
 * A Xyo Encoded Md2 hash
 *
 * @major 0x03
 * @minor 0x01
 */
class XyoMd2 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x01
        override val standardDigestKey: String = "MD2"

        override fun readSize(byteArray: ByteArray): Int {
            return 16
        }
    }
}