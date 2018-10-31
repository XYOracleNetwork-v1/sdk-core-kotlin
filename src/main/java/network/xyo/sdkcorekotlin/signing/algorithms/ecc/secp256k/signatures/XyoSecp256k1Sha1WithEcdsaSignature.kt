package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature
import java.math.BigInteger

/**
 * A Xyo Signature made using EC with the Secp256K1 curve with SHA1.
 *
 * @major 0x05
 * @minor 0x02
 */
class XyoSecp256k1Sha1WithEcdsaSignature(val r : BigInteger, val s : BigInteger) : XyoEcdsaSignature(r, s) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoEcdsaSignatureProvider() {
        override val minor: Byte = 0x02

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val rAndS = getRAndS(byteArray)
            return XyoSecp256k1Sha1WithEcdsaSignature(rAndS.r, rAndS.s)
        }
    }
}