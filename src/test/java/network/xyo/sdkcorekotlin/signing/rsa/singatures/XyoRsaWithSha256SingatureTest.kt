package network.xyo.sdkcorekotlin.signing.rsa.singatures

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Singature

class XyoRsaWithSha256SingatureTest : XyoTestBase() {

    @kotlin.test.Test
    fun testRsaWithSha256SingatureTest () {
        val starting = XyoRsaWithSha256Singature(byteArrayOf(0x00, 0x01, 0x02))
        val startingPacked = starting.untyped

        val endingUnpacked = XyoRsaWithSha256Singature.createFromPacked(startingPacked)

        assertXyoObject(starting, endingUnpacked)
    }
}