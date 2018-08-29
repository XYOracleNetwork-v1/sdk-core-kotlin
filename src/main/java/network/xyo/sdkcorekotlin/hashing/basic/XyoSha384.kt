package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

class XyoSha384 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseCreator() {
        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(48)
        }

        override val minor: Byte
            get() = 0x06

        override val standardDigestKey: String
            get() = "SHA-384"
    }
}