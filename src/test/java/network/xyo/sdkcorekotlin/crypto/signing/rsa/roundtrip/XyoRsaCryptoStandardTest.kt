package network.xyo.sdkcorekotlin.crypto.signing.rsa.roundtrip

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaWithSha256
import org.junit.Assert
import org.junit.Test

class XyoRsaCryptoStandardTest : XyoTestBase() {

    @Test
    fun testRsaCryptoStandard () {
        runBlocking {
            val dataToSign = "00".hexStringToByteArray()
            val assertedPublic = "000D8200CD1918DA1DF79B95977029E9DF2E272340B0725FE3EFB9670944F3F2C471B1C7B120D582FEE0721589DDF2937D247C53154575306A5F5BC8C9D9707DBB81E80EB18DE4C255CE9C1B9CAA90513673D9A71B8085E2956BDE3D63D9A40626304C0348159A9CF3DE4BE13E9369F7D263753B8DB1461F65E8EF14EB60CB081AB4EFAD".hexStringToByteArray()
            val assertedPrivate = "40FF010582008351F071A802646211C6C56F7370EFAA48BF315C686B93C07F54E105ADF6462860276FCE2237BF943EE4176F8C0F127378093311BEEA33B08A5D6903784DA330AFB470A887108445C19829B95E25DC84D1990EA9DE4023657AEE18214E9A8457CEA89863682BC770AA4DBDCEF72649AE9EABDF0718BB8B2E307CE95F5FE920D900CD1918DA1DF79B95977029E9DF2E272340B0725FE3EFB9670944F3F2C471B1C7B120D582FEE0721589DDF2937D247C53154575306A5F5BC8C9D9707DBB81E80EB18DE4C255CE9C1B9CAA90513673D9A71B8085E2956BDE3D63D9A40626304C0348159A9CF3DE4BE13E9369F7D263753B8DB1461F65E8EF14EB60CB081AB4EFAD".hexStringToByteArray()
            val rsa = XyoRsaWithSha256.newInstance(assertedPrivate)
            val sig = rsa.signData(dataToSign).await()

            Assert.assertArrayEquals(assertedPublic, rsa.publicKey.bytesCopy)
            Assert.assertArrayEquals(assertedPrivate, rsa.privateKey.bytesCopy)
            Assert.assertTrue(XyoRsaWithSha256.verifySign(sig, dataToSign, XyoRsaPublicKey.getInstance(assertedPublic)).await())
        }
    }
}