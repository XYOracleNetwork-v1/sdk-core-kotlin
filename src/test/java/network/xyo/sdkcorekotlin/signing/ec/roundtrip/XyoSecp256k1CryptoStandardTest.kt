package network.xyo.sdkcorekotlin.signing.ec.roundtrip

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import org.junit.Assert
import org.junit.Test

class XyoSecp256k1CryptoStandardTest : XyoTestBase() {

    @Test
    fun testSecp256k1CryptoStandard () {
        runBlocking {
            val dataToSign = "00".hexStringToByteArray()
            val assertedPublic = "DC26168A6630A280E7152FD2749F60BC59EDAC0544276B7F55C91FC57141E4E510D55149DEB84941BC68EC863A9288A65EB485B631F08BD9DC0AA65F5F5E2D12".hexStringToByteArray()
            val assertedPrivate = "002300DECCC9FA76EF2D0D90D5C5C9807C25E5429C5202D35A8F5D5C9A3CD7DE0B26EF".hexStringToByteArray()
            val testedSig = "4320792EAB1E531D40529536D37ECAC25FE8AD798A5BC3297595BC6E64AD2BBFA9092005B7D6E324555EA855C12B5FC5CB0C2527F15836CCFC622963E8E1783ECFD509".hexStringToByteArray()

            val ec = XyoSha256WithSecp256K(XyoEcPrivateKey.createFromPacked(assertedPrivate))
            val sig = XyoSecp256k1Sha256WithEcdsaSignature.createFromPacked(testedSig) as XyoSecp256k1Sha256WithEcdsaSignature

            Assert.assertArrayEquals(assertedPublic, ec.publicKey.untyped)
            Assert.assertArrayEquals(assertedPrivate, ec.privateKey.untyped)
            Assert.assertTrue(XyoSha256WithSecp256K.verifySign(sig, dataToSign, XyoSecp256K1UnCompressedPublicKey.createFromPacked(ec.publicKey.untyped)).await())
        }
    }
}
