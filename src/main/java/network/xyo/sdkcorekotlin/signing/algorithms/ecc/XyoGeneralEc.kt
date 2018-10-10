package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECParameterSpec
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
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

    /**
     * The spec and curve
     */
    abstract val spec : ECParameterSpec

    /**
     * Turn ec public and private keys into XyoObject based keys. This is used for packing and unpacking.
     *
     * @param ecPublicKey The public key to convert
     * @param ecPrivateKey The private key to convert
     */
    abstract fun ecKeyPairToXyoKeyPair (ecPublicKey : ECPublicKey, ecPrivateKey : ECPrivateKey) : KeyPair

    private fun generateKeyFromPrivate (privateKey: XyoEcPrivateKey) : KeyPair {
        val keyGenerator : KeyFactory = KeyFactory.getInstance("EC")
        val publicKey = keyGenerator.generatePublic(getSpecFromPrivateKey(privateKey)) as ECPublicKey

        return ecKeyPairToXyoKeyPair(publicKey, privateKey)
    }

    private fun getSpecFromPrivateKey (privateKey: XyoEcPrivateKey) : ECPublicKeySpec {
        val domain = ECDomainParameters(spec.curve, spec.g, spec.n, spec.h)

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
        val keyGenerator : KeyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider())
        keyGenerator.initialize(spec)

        val generatedKeyPair = keyGenerator.genKeyPair()
        val ecPublic =  generatedKeyPair.public as ECPublicKey
        val ecPrivate = generatedKeyPair.private as ECPrivateKey

        return ecKeyPairToXyoKeyPair(ecPublic, ecPrivate)
    }
}