package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult


class XyoSha1 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseCreator() {
        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(20)
        }

        override val minor: Byte
            get() = 0x03

        override val standardDigestKey: String
            get() = "SHA-1"
    }
}