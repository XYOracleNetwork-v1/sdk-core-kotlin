package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature

/**
 * A Xyo Signature made using EC with the Secp256K curve with SHA256.
 *
 * @major 0x05
 * @minor 0x01
 */
class XyoSecp256k1Sha256WithEcdsaSignature(rawSignature : ByteArray) : XyoEcdsaSignature(rawSignature) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoEcdsaSignatureProvider() {
        override val minor: Byte = 0x01
    }
}