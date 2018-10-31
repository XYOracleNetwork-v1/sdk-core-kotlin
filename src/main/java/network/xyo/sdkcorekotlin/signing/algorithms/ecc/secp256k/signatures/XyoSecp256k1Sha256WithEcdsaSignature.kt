package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature
import java.math.BigInteger

/**
 * A Xyo Signature made using EC with the Secp256K curve with SHA256.
 *
 * @major 0x05
 * @minor 0x01
 */
class XyoSecp256k1Sha256WithEcdsaSignature(val r : BigInteger, val s : BigInteger) : XyoEcdsaSignature(r, s) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoEcdsaSignatureProvider() {
        override val minor: Byte = 0x01

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val rAndS = getRAndS(byteArray)
            return XyoSecp256k1Sha256WithEcdsaSignature(rAndS.r, rAndS.s)
        }
    }
}