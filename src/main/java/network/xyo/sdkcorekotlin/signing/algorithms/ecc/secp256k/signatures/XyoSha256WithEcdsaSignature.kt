package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature

class XyoSha256WithEcdsaSignature(rawSignature : ByteArray) : XyoEcdsaSignature(rawSignature) {
    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoEcdsaSignatureCreator() {
        override val minor: Byte
            get() = 0x01
    }
}