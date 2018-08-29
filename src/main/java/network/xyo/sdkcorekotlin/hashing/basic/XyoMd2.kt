package network.xyo.sdkcorekotlin.hashing.basic

import network.xyo.sdkcorekotlin.XyoResult

class XyoMd2 (pastHash : ByteArray): XyoBasicHashBase(pastHash) {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    companion object : XyoBasicHashBaseCreator() {
        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(16)
        }

        override val minor: Byte
            get() = 0x01

        override val standardDigestKey: String
            get() = "MD2"
    }
}