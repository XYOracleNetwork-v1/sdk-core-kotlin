package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec


/**
 * A base class for all EC operations using the Secp256K curve.
 */
abstract class XyoEcSecp256K (privateKey: XyoObject?) : XyoGeneralEc(privateKey) {
    override val curve: ECNamedCurveParameterSpec
        get() = ECNamedCurveTable.getParameterSpec("secp256k1")

    override val spec: ECParameterSpec
        get() = XyoSecp256K1UnCompressedPublicKey.ecPramSpec

    override val publicKey: XyoObject
        get() = keyPair.public as XyoSecp256K1UnCompressedPublicKey

    override val privateKey: XyoObject
        get() = keyPair.private as XyoEcPrivateKey

    override fun ecKeyPairToXyoKeyPair(ecPublicKey: ECPublicKey, ecPrivateKey : ECPrivateKey): KeyPair {
        return KeyPair(
                object : XyoSecp256K1UnCompressedPublicKey() {
                    override val x: BigInteger
                        get() = ecPublicKey.w.affineX

                    override val y: BigInteger
                        get() = ecPublicKey.w.affineY
                },
                XyoEcPrivateKey(ecPrivateKey.s, XyoSecp256K1UnCompressedPublicKey.ecPramSpec))
    }
}