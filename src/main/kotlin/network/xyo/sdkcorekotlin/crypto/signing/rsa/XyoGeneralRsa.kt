package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec

/**
 * A base class for all RSA cryptography.
 *
 * @param keySize The size of the keypair to generate.
 */
@ExperimentalStdlibApi
abstract class XyoGeneralRsa(private val keySize : Int, privateKey: XyoRsaPrivateKey?) : XyoSigner() {

    /**
     * The Java Signature object when creating signaturePacking. This is used when switching between
     * SHA1withRSA, SHA256withRSA etc.
     */
    abstract val signature : Signature

    override val publicKey: XyoPublicKey
        get() {
            return keyPair.public as XyoRsaPublicKey
        }

    open val keyPair: KeyPair = if (privateKey != null) {
        generateKeyPairFromPrivate(privateKey)
    } else {
        generateNewKeyPair()
    }

    override val privateKey: XyoRsaPrivateKey
        get() = (keyPair.private as XyoRsaPrivateKey)



    private fun generateKeyPairFromPrivate (encodedPrivateKey: XyoRsaPrivateKey) : KeyPair {
        val keyGenerator: KeyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyGenerator.generatePublic(getSpecFromPrivateKey(encodedPrivateKey)) as RSAPublicKey

        return KeyPair(XyoRsaPublicKey(publicKey.modulus), encodedPrivateKey)
    }

    private fun generateNewKeyPair () : KeyPair {
        val keyGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyGenerator.initialize(keySize)

        val standardKeyPair = keyGenerator.genKeyPair()
        val publicKey = standardKeyPair.public as RSAPublicKey
        val privateKey = standardKeyPair.private as RSAPrivateKey

        return KeyPair(XyoRsaPublicKey(publicKey.modulus), XyoRsaPrivateKey(privateKey.modulus, privateKey.privateExponent))
    }

    private fun getSpecFromPrivateKey (encodedPrivateKey: XyoRsaPrivateKey) : RSAPublicKeySpec {
        return RSAPublicKeySpec(encodedPrivateKey.modulus, BigInteger(RSA_PUBLIC_EXPONENT))
    }

    companion object {
        /**
         * @note All Xyo Rsa Public Key operations use the modulus 0x0100001
         */
        val RSA_PUBLIC_EXPONENT = byteArrayOf(0x01, 0x00, 0x01)
    }
}