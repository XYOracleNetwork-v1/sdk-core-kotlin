package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import java.math.BigInteger
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec

/**
 * A base class for a all RSA cryptography.
 *
 * @param keySize The size of the keypair to generate.
 */

abstract class XyoGeneralRsa(private val keySize : Int, privateKey: XyoObject?) : XyoSigner() {

    /**
     * The Java Signature object when creating signaturePacking. This is used when switching between
     * SHA1withRSA, SHA256withRSA ect.
     */
    abstract val signature : Signature

    override val publicKey: XyoObject
        get() {
            val rsaKeyPair = keyPair.public as? XyoRsaPublicKey
            if (rsaKeyPair != null) {
                return rsaKeyPair
            }
            throw Exception("Key can not be casted!")
        }

    open val keyPair: KeyPair = generateKeyPair(privateKey)

    override val privateKey: XyoObject
        get() = (keyPair.private as XyoRsaPrivateKey)

    private fun generateKeyPair(privateKey: XyoObject?): KeyPair {
        if (privateKey != null) {
            return generateKeyPairFromPrivate(privateKey)
        }
        return generateNewKeyPair()
    }

    private fun generateKeyPairFromPrivate (encodedPrivateKey: XyoObject) : KeyPair {
        val keyGenerator: KeyFactory = KeyFactory.getInstance("RSA")
        val privateKey = encodedPrivateKey as XyoRsaPrivateKey
        val publicKey = keyGenerator.generatePublic(getSpecFromPrivateKey(privateKey)) as RSAPublicKey

        return KeyPair(XyoRsaPublicKey(publicKey.modulus), privateKey)
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
        return RSAPublicKeySpec(encodedPrivateKey.modulus, BigInteger(byteArrayOf(0x01, 0x00, 0x01)))
    }
}