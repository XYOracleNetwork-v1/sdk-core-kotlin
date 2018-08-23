package network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures

import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaSignature

class XyoRsaWithSha256Singature(rawSignature: ByteArray) : XyoRsaSignature(rawSignature) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoRsaSignatureCreator() {
        override val minor: Byte
            get() = 0x09
    }
}