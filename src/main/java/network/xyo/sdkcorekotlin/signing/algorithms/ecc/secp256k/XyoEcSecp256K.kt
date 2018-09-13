package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

/**
 * A base class for all EC operations using the Secp256K curve.
 */
abstract class XyoEcSecp256K : XyoGeneralEc() {
    /**
     * The generated public key.
     */
    open val keyPair: KeyPair = generateKeyPair()
    override val publicKey: XyoObject
        get() {
            val ecPublicKey = keyPair.public as? XyoSecp256K1UnCompressedPublicKey
            if (ecPublicKey != null) {
                return ecPublicKey
            }
            throw Exception("Can not cast public key!")
        }

    private fun generateKeyPair(): KeyPair {
        keyGenerator.initialize(XyoSecp256K1UnCompressedPublicKey.ecPramSpec)
        KeyFactory.getInstance("EC")
        val generatedKeyPair = keyGenerator.genKeyPair()
        val ecPublic =  generatedKeyPair.public as? ECPublicKey
        val ecPrivate = generatedKeyPair.private as? ECPrivateKey

        if (ecPublic != null && ecPrivate != null) {
            return KeyPair(
                    object : XyoSecp256K1UnCompressedPublicKey() {
                        override val x: BigInteger
                            get() = ecPublic.w.affineX

                        override val y: BigInteger
                            get() = ecPublic.w.affineY
                    },
                    XyoEcPrivateKey(ecPrivate.s, XyoSecp256K1UnCompressedPublicKey.ecPramSpec))
        }
        throw Exception("No public and private keys!")
    }
}