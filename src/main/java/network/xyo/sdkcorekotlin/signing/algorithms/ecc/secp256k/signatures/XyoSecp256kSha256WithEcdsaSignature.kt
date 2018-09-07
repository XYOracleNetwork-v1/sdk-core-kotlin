package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature

/**
 * A Xyo Signature made using EC with the Secp256K curve with SHA256.
 *
 * @major 0x05
 * @minor 0x01
 */
class XyoSecp256kSha256WithEcdsaSignature(rawSignature : ByteArray) : XyoEcdsaSignature(rawSignature) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))

    companion object : XyoEcdsaSignatureProvider() {
        override val minor: Byte = 0x01
    }
}