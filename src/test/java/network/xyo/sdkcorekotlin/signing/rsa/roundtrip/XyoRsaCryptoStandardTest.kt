package network.xyo.sdkcorekotlin.signing.rsa.roundtrip

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import org.junit.Assert
import org.junit.Test

class XyoRsaCryptoStandardTest : XyoTestBase() {

    @Test
    fun testRsaCryptoStandard () {
        runBlocking {
            val dataToSign = "00".hexStringToByteArray()
            val assertedSig = "0042A38614CDEF6D4498CF57558CB8E99A10B477E860648F8DF41230CDAAD17FF55E8C1EFF834F388BC1C771139AF2E2D4D9DEFBAA1E486142630BDB40715C049468".hexStringToByteArray()
            val assertedPublic = "004300B6EE5F33EAA27FF937140A4CB24DB224AD479A1A6CC82750A09C112DF6310289209FDC09F9B2892A965A30E1183F8032EBFFAB396DAC190EF820C859B8B54807".hexStringToByteArray()
            val assertedPrivate = "0084411DE668793BE8C3B220668E286B1C77F9B6B8F55F4C588AD48AEBF782E3B2AF0F23E7A6B357BD6044519C4A17FE291148778525556FC8EAF4CD9BAB1D9D5FF38100B6EE5F33EAA27FF937140A4CB24DB224AD479A1A6CC82750A09C112DF6310289209FDC09F9B2892A965A30E1183F8032EBFFAB396DAC190EF820C859B8B54807".hexStringToByteArray()
            val rsa = XyoRsaWithSha256(XyoRsaPrivateKey.createFromPacked(assertedPrivate))
            val sig = rsa.signData(dataToSign).await()

            Assert.assertArrayEquals(assertedPublic, rsa.publicKey.untyped)
            Assert.assertArrayEquals(assertedPrivate, rsa.privateKey.untyped)
            Assert.assertArrayEquals(assertedSig,  sig.untyped)
            Assert.assertTrue(XyoRsaWithSha256.verifySign(sig, dataToSign, XyoRsaPublicKey.createFromPacked(assertedPublic)).await())
        }
    }
}