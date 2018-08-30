package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

class XyoSha512 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseProvider() {
        override val standardDigestKey: String = "SHA-512"
        override val minor: Byte = 0x07

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(64)
        }
    }
}