package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature

class XyoSha1WithEcdsaSignature(rawSignature : ByteArray) : XyoEcdsaSignature(rawSignature) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoEcdsaSignatureProvider() {
        override val minor: Byte = 0x02
    }
}