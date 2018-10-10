package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys

import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoSecp256k1
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import java.security.AlgorithmParameters
import java.security.KeyPairGenerator
import java.security.Security
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.Security.addProvider
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.security.interfaces.ECPublicKey
import java.security.spec.ECPublicKeySpec


/**
 * A Xyo Public Key made using EC with the Secp256K1 curve.
 *
 * @major 0x04
 * @minor 0x01
 */
abstract class XyoSecp256K1UnCompressedPublicKey : XyoUncompressedEcPublicKey() {
    override val ecSpec: ECParameterSpec = ecPramSpec
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoUncompressedEcPublicKeyProvider() {
        override val minor: Byte = 0x01

        override val ecPramSpec: ECParameterSpec
            get() {
                val parameters = AlgorithmParameters.getInstance("EC", BouncyCastleProvider())
                parameters.init(ECGenParameterSpec("secp256k1"))
                return parameters.getParameterSpec(ECParameterSpec::class.java)
            }
    }
}