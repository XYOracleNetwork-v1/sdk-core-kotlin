package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import org.junit.Test

class XyoSignatureSetTest : XyoTestBase() {

    @Test
    fun testXyoSignatureSetTest() {
        XyoRsaWithSha256Signature.enable()
        XyoSecp256k1Sha256WithEcdsaSignature.enable()

        val expected = XyoSignatureSet(
                arrayOf(
                        XyoRsaWithSha256Signature(byteArrayOf(0x00))

                )
        )

        val unpacked = XyoSignatureSet.createFromPacked(expected.untyped)
        assertXyoObject(expected, unpacked)
    }
}