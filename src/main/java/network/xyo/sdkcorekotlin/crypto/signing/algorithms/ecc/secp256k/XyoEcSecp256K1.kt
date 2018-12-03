package network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.jce.spec.ECParameterSpec
import java.math.BigInteger
import java.security.KeyPair


/**
 * A base class for all EC operations using the Secp256K curve.
 */
abstract class XyoEcSecp256K1 (privateKey: ECPrivateKey?) : XyoGeneralEc(privateKey) {
    override val spec: ECParameterSpec
        get() = ecSpec

    override val publicKey: XyoPublicKey
        get() = keyPair.public as XyoPublicKey

    override val privateKey: XyoEcPrivateKey
        get() = keyPair.private as XyoEcPrivateKey

    override fun ecKeyPairToXyoKeyPair(ecPublicKey: ECPublicKey, ecPrivateKey : ECPrivateKey): KeyPair {
        return KeyPair(
                object : XyoUncompressedEcPublicKey() {
                    override val x: BigInteger
                        get() = ecPublicKey.q.xCoord.toBigInteger()

                    override val y: BigInteger
                        get() = ecPublicKey.q.yCoord.toBigInteger()

                    override val ecSpec: ECParameterSpec = spec
                },
                XyoEcPrivateKey.getInstanceFromQ(ecPrivateKey.d, spec)
        )
    }

    companion object {
        val ecCurve : ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1")
        val ecDomainParameters = ECDomainParameters(ecCurve.curve, ecCurve.g, ecCurve.n)
        val ecSpec: ECParameterSpec = ECParameterSpec(ecCurve.curve, ecCurve.g, ecCurve.n)
    }
}