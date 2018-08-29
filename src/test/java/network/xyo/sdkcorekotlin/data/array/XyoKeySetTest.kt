package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha1WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1CompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey

class XyoKeySetTest : XyoTestBase() {

    @kotlin.test.Test
    fun testKeySet() {
        XyoSha256WithSecp256K.enable()
        XyoSha1WithSecp256K.enable()
        XyoSecp256K1CompressedPublicKey.enable()
        XyoRsaPublicKey.enable()

        val keySetExpected = XyoKeySet(arrayOf(
                XyoSigningObject.getCreator(0x02)!!.newInstance().publicKey
        ))

        val keySetCreated = XyoKeySet.createFromPacked(keySetExpected.untyped)

        assertXyoObject(keySetExpected, keySetCreated)
    }

}