package network.xyo.sdkcorekotlin.crypto.signing.ecdsa

import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.jce.spec.ECPublicKeySpec
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator

abstract class XyoGeneralEc (privateKey: ECPrivateKey?) : XyoSigner() {

    val keyPair: KeyPair = if (privateKey != null) {
            generateKeyFromPrivate(privateKey)
        } else {
            generateNewKeyPair()
        }

    abstract val spec : ECParameterSpec
    abstract fun ecKeyPairToXyoKeyPair (ecPublicKey : ECPublicKey, ecPrivateKey : ECPrivateKey) : KeyPair

    private fun generateKeyFromPrivate (privateKey: ECPrivateKey) : KeyPair {
        val keyGenerator : KeyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider())
        val publicKey = keyGenerator.generatePublic(getSpecFromPrivateKey(privateKey)) as ECPublicKey

        return ecKeyPairToXyoKeyPair(publicKey, privateKey)
    }

    private fun getSpecFromPrivateKey (privateKey: ECPrivateKey) : ECPublicKeySpec {
        val domain = ECDomainParameters(spec.curve, spec.g, spec.n, spec.h)
        return ECPublicKeySpec(domain.g.multiply(privateKey.d), privateKey.parameters)
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