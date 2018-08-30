package network.xyo.sdkcorekotlin.signing.ec.signatures

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha256WithEcdsaSignature

class XyoSha256WithEcdsaSignatureTest : XyoTestBase() {

    @kotlin.test.Test
    fun testXyoSha256WithEcdsaSignature () {
        XyoSha256WithEcdsaSignature.enable()
        val starting = XyoSha256WithEcdsaSignature(byteArrayOf(0x00, 0x01, 0x02))
        val startingPacked = starting.untyped
        val endingUnpacked = XyoSha256WithEcdsaSignature.createFromPacked(startingPacked.value!!)

        assertXyoObject(starting, endingUnpacked.value!!)
    }
}