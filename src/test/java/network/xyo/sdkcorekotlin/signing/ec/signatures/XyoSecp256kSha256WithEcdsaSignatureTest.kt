package network.xyo.sdkcorekotlin.signing.ec.signatures

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import org.junit.Test

class XyoSecp256kSha256WithEcdsaSignatureTest : XyoTestBase() {

    @Test
    fun testXyoSha256WithEcdsaSignature () {
        XyoSecp256k1Sha256WithEcdsaSignature.enable()
        val starting = XyoSecp256k1Sha256WithEcdsaSignature(byteArrayOf(0x00, 0x01, 0x02))
        val startingPacked = starting.untyped
        val endingUnpacked = XyoSecp256k1Sha256WithEcdsaSignature.createFromPacked(startingPacked)

        assertXyoObject(starting, endingUnpacked)
    }
}