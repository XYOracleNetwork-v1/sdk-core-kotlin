package network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaSignature

class XyoRsaWithSha256Singature(rawSignature: ByteArray) : XyoRsaSignature(rawSignature) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoRsaSignatureProvider() {
        override val minor: Byte = 0x09
    }
}