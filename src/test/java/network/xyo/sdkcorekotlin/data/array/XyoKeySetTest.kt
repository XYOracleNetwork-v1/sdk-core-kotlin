package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha1WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey

class XyoKeySetTest : XyoTestBase() {

    @kotlin.test.Test
    fun testKeySet() {
        XyoSha256WithSecp256K.enable()
        XyoSha1WithSecp256K.enable()
        XyoSecp256K1UnCompressedPublicKey.enable()
        XyoRsaPublicKey.enable()

        val keySetExpected = XyoKeySet(arrayOf(
                XyoSigner.getCreator(0x02)!!.newInstance().publicKey,
                XyoSigner.getCreator(0x02)!!.newInstance().publicKey,
                XyoSigner.getCreator(0x02)!!.newInstance().publicKey
        ))

        val keySetCreated = XyoKeySet.createFromPacked(keySetExpected.untyped)

        assertXyoObject(keySetExpected, keySetCreated)
    }

}