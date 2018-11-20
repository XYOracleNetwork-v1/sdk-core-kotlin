package network.xyo.sdkcorekotlin.signing.ec.roundtrip

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.EC_PRIVATE_KEY
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.EC_SIGNATURE
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoEcSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import org.junit.Assert
import org.junit.Test

class XyoSecp256k1CryptoStandardTest : XyoTestBase() {

    @Test
    fun testSecp256k1CryptoStandard () {
        runBlocking {
            val dataToSign = "00".hexStringToByteArray()
            val assertedPublic = "DC26168A6630A280E7152FD2749F60BC59EDAC0544276B7F55C91FC57141E4E510D55149DEB84941BC68EC863A9288A65EB485B631F08BD9DC0AA65F5F5E2D12".hexStringToByteArray()
            val assertedPrivate = "00DECCC9FA76EF2D0D90D5C5C9807C25E5429C5202D35A8F5D5C9A3CD7DE0B26EF".hexStringToByteArray()
            val ec = XyoSha256WithSecp256K(XyoEcPrivateKey.getInstance(XyoObjectCreator.createObject(EC_PRIVATE_KEY, assertedPrivate), XyoEcSecp256K.getSpec()))
            val sig = ec.signData(dataToSign).await()

            Assert.assertArrayEquals(assertedPublic, XyoObjectCreator.getObjectValue(ec.publicKey.self))
            Assert.assertArrayEquals(assertedPrivate, XyoObjectCreator.getObjectValue(ec.privateKey.self))
            Assert.assertTrue(XyoSha256WithSecp256K.verifySign(sig, dataToSign, ec.publicKey).await())
        }
    }
}
