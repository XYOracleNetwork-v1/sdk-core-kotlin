package network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECParameterSpec
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec


abstract class XyoGeneralEc (privateKey: ECPrivateKey?) : XyoSigner() {

    val keyPair: KeyPair = generateKeyPair(privateKey)


    abstract val spec : ECParameterSpec


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

    private fun generateKeyPair(encodedPrivateKey: ECPrivateKey?): KeyPair {
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