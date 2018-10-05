package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature

/**
 * A Xyo Signature made using EC with the Secp256K1 curve with SHA1.
 *
 * @major 0x05
 * @minor 0x02
 */
class XyoSecp256k1Sha1WithEcdsaSignature(rawSignature : ByteArray) : XyoEcdsaSignature(rawSignature) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoEcdsaSignatureProvider() {
        override val minor: Byte = 0x02
    }
}