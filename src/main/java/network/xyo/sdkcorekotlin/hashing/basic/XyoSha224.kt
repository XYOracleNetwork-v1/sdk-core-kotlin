package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

/**
 * A Xyo Encoded Sha224 hash
 *
 * @major 0x03
 * @minor 0x04
 */
class XyoSha224 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x04
        override val standardDigestKey: String = "SHA-224"
        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(20)
        }
    }
}