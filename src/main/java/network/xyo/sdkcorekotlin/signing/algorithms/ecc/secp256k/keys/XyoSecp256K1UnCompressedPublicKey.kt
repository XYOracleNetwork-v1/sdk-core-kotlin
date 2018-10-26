package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys

import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.AlgorithmParameters
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec


/**
 * A Xyo Public Key made using EC with the Secp256K1 curve.
 *
 * @major 0x04
 * @minor 0x01
 */
abstract class XyoSecp256K1UnCompressedPublicKey : XyoUncompressedEcPublicKey() {
    override val ecSpec: ECParameterSpec
        get() = ecPramSpec

    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoUncompressedEcPublicKeyProvider() {
        override val minor: Byte = 0x01

        override val ecPramSpec: ECParameterSpec = getSpec()

//        override val ecPramSpec: ECParameterSpec
//            get() {
//                val parameters = AlgorithmParameters.getInstance("EC", BouncyCastleProvider())
//                parameters.init(ECGenParameterSpec("secp256k1"))
//                return parameters.getParameterSpec(ECParameterSpec::class.java)
//            }

        private fun getSpec() : ECParameterSpec {
            val parameters = AlgorithmParameters.getInstance("EC", BouncyCastleProvider())
            parameters.init(ECGenParameterSpec("secp256k1"))
            return parameters.getParameterSpec(ECParameterSpec::class.java)
        }
    }
}