package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys

import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import java.security.AlgorithmParameters
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec

/**
 * A Xyo Public Key made using EC with the Secp256K curve.
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
                val parameters = AlgorithmParameters.getInstance("EC")
                parameters.init(ECGenParameterSpec("secp256k1"))
                return parameters.getParameterSpec(ECParameterSpec::class.java)
            }
    }
}