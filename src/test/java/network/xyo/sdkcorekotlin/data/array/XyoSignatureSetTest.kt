package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature

class XyoSignatureSetTest : XyoTestBase() {

    @kotlin.test.Test
    fun testXyoSignatureSetTest() {
        XyoRsaWithSha256Signature.enable()
        XyoSha256WithEcdsaSignature.enable()

        val expected = XyoSignatureSet(
                arrayOf(
                        XyoSha256WithEcdsaSignature(byteArrayOf(0x00, 0x01, 0x02))

                )
        )

        val unpacked = XyoSignatureSet.createFromPacked(expected.untyped.value!!)
        assertXyoObject(expected, unpacked.value!!)
    }
}