package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1CompressedPublicKey
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

abstract class XyoEcSecp256K : XyoGeneralEc() {
    open val keyPair: KeyPair = generateKeyPair()
    override val publicKey: XyoObject
        get() = keyPair.public as XyoSecp256K1CompressedPublicKey

    private fun generateKeyPair(): KeyPair {
        keyGenerator.initialize(XyoSecp256K1CompressedPublicKey.ecPramSpec)
        KeyFactory.getInstance("EC")
        val generatedKeyPair = keyGenerator.genKeyPair()
        val ecPublic =  generatedKeyPair.public as? ECPublicKey
        val ecPrivate = generatedKeyPair.private as? ECPrivateKey

        if (ecPublic != null && ecPrivate != null) {
            return KeyPair(
                    object : XyoSecp256K1CompressedPublicKey() {
                        override val x: BigInteger
                            get() = ecPublic.w.affineX

                        override val y: BigInteger
                            get() = ecPublic.w.affineY
                    },
                    XyoEcPrivateKey(ecPrivate.s, XyoSecp256K1CompressedPublicKey.ecPramSpec))
        }
        throw Exception()
    }
}