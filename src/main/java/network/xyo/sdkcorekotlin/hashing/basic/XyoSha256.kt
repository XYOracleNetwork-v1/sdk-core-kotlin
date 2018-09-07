package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

/**
 * A Xyo Encoded Sha256 hash
 *
 * @major 0x03
 * @minor 0x05
 */
class XyoSha256 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x05
        override val standardDigestKey: String = "SHA-256"

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(32)
        }
    }
}
