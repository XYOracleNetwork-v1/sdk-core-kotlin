package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

/**
 * A Xyo Encoded Md2 hash
 *
 * @major 0x03
 * @minor 0x01
 */
class XyoMd2 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseProvider() {
        override val minor: Byte = 0x01
        override val standardDigestKey: String = "MD2"

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(16)
        }
    }
}