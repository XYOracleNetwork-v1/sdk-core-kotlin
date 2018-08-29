package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

class XyoSha512 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseCreator() {
        override val minor: Byte
            get() = 0x07

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(64)
        }

        override val standardDigestKey: String
            get() = "SHA-512"
    }
}