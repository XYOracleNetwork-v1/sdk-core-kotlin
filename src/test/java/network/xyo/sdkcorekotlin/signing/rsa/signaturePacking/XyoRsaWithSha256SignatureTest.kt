package network.xyo.sdkcorekotlin.signing.rsa.signaturePacking

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import org.junit.Test

class XyoRsaWithSha256SignatureTest : XyoTestBase() {

    @Test
    fun testRsaWithSha256SignaturesTest () {
        val starting = XyoRsaWithSha256Signature(byteArrayOf(0x00, 0x01, 0x02))
        val startingPacked = starting.untyped
        val endingUnpacked = XyoRsaWithSha256Signature.createFromPacked(startingPacked)

        assertXyoObject(starting, endingUnpacked)
    }
}