package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import java.security.*
import java.security.interfaces.RSAPublicKey

/**
 * A base class for a all RSA cryptography.
 *
 * @param keySize The size of the keypair to generate.
 */

abstract class XyoGeneralRsa(private val keySize : Int) : XyoSigner() {
    private val keyGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")

    /**
     * The Java Signature object when creating signatures. This is used when switching between
     * SHA1withRSA, SHA256withRSA ect.
     */
    abstract val signature : Signature

    override val publicKey: XyoResult<XyoObject>
        get() {
            val rsaKeyPair = keyPair.public as? XyoRsaPublicKey
            if (rsaKeyPair != null) {
                return XyoResult(rsaKeyPair)
            }
            return XyoResult(XyoError(this.toString(), "Can not be casted to XyoRsaPublicKey!"))
        }

    open val keyPair: KeyPair = generateKeyPair()

    private fun generateKeyPair(): KeyPair {
        keyGenerator.initialize(keySize)
        val standardKeyPair = keyGenerator.genKeyPair()
        val publicKey = standardKeyPair.public as? RSAPublicKey

        if (publicKey != null) {
            return KeyPair(XyoRsaPublicKey(publicKey.modulus), standardKeyPair.private)
        }

        throw Exception()
    }
}