package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec


/**
 * A base class for all EC operations using the Secp256K curve.
 */
abstract class XyoEcSecp256K (privateKey: ECPrivateKey?) : XyoGeneralEc(privateKey) {
    override val spec: org.bouncycastle.jce.spec.ECParameterSpec
        get() = org.bouncycastle.jce.spec.ECParameterSpec(ecCurve.curve, ecCurve.g, ecCurve.n)

    override val publicKey: XyoPublicKey
        get() = keyPair.public as XyoPublicKey

    override val privateKey: XyoEcPrivateKey
        get() = keyPair.private as XyoEcPrivateKey

    @ExperimentalUnsignedTypes
    override fun ecKeyPairToXyoKeyPair(ecPublicKey: ECPublicKey, ecPrivateKey : ECPrivateKey): KeyPair {
        return KeyPair(
                object : XyoUncompressedEcPublicKey() {
                    override val x: BigInteger
                        get() = ecPublicKey.w.affineX

                    override val y: BigInteger
                        get() = ecPublicKey.w.affineY

                    override val ecSpec: java.security.spec.ECParameterSpec
                        get() = getSpec()
                },
                object : XyoEcPrivateKey(getSpec()) {
                    override fun getS(): BigInteger {
                       return ecPrivateKey.s
                    }
                }
        )
    }

    companion object {
        val ecCurve = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName("secp256k1")
        val ecDomainParameters = ECDomainParameters(ecCurve.curve, ecCurve.g, ecCurve.n)

        fun getSpec() : ECParameterSpec {
            val parameters = AlgorithmParameters.getInstance("EC", BouncyCastleProvider())
            parameters.init(ECGenParameterSpec("secp256k1"))
            return parameters.getParameterSpec(ECParameterSpec::class.java)
        }
    }
}