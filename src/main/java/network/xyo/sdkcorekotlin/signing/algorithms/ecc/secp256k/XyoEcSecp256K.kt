package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECParameterSpec
import java.math.BigInteger
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey


/**
 * A base class for all EC operations using the Secp256K curve.
 */
abstract class XyoEcSecp256K (privateKey: XyoObject?) : XyoGeneralEc(privateKey) {
    override val spec: ECParameterSpec
        get() = ECNamedCurveTable.getParameterSpec("secp256k1")

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