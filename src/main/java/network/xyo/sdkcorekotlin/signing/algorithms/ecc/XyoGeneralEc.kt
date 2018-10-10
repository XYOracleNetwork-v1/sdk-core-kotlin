package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec

/**
 * A base class for all EC crypto operations.
 */
abstract class XyoGeneralEc (privateKey: XyoObject?) : XyoSigner() {
    /**
     * The generated public key.
     */
    val keyPair: KeyPair = generateKeyPair(privateKey)


    abstract val curve : ECNamedCurveParameterSpec
    abstract val spec : ECParameterSpec
    abstract fun ecKeyPairToXyoKeyPair (ecPublicKey : ECPublicKey, ecPrivateKey : ECPrivateKey) : KeyPair

    private fun generateKeyFromPrivate (privateKey: XyoEcPrivateKey) : KeyPair {
        val keyGenerator : KeyFactory = KeyFactory.getInstance("EC")
        val publicKey = keyGenerator.generatePublic(getSpecFromPrivateKey(privateKey)) as ECPublicKey

        return ecKeyPairToXyoKeyPair(publicKey, privateKey)
    }

    private fun getSpecFromPrivateKey (privateKey: XyoEcPrivateKey) : ECPublicKeySpec {
        val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)

        val q = domain.g.multiply(privateKey.s)
        val point = ECPoint(q.x.toBigInteger(), q.y.toBigInteger())

        return ECPublicKeySpec(point,  privateKey.params)
    }

    private fun generateKeyPair(encodedPrivateKey: XyoObject?): KeyPair {
        if (encodedPrivateKey != null) {
            return generateKeyFromPrivate(encodedPrivateKey as XyoEcPrivateKey)
        }
        return generateNewKeyPair()
    }

    private fun generateNewKeyPair () : KeyPair {
        val keyGenerator : KeyPairGenerator = KeyPairGenerator.getInstance("EC")
        keyGenerator.initialize(spec)

        val generatedKeyPair = keyGenerator.genKeyPair()
        val ecPublic =  generatedKeyPair.public as ECPublicKey
        val ecPrivate = generatedKeyPair.private as ECPrivateKey

        return ecKeyPairToXyoKeyPair(ecPublic, ecPrivate)
    }
}