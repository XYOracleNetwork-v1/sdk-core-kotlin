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

    init {
        println ("X: " + x.toByteArray().toHexString())
        println ("X: " + y.toByteArray().toHexString())
    }

    companion object : XyoUncompressedEcPublicKeyProvider() {
        override val minor: Byte = 0x01

        override val ecPramSpec: ECParameterSpec = getSpec()

        private fun getSpec() : ECParameterSpec {
            val parameters = AlgorithmParameters.getInstance("EC", BouncyCastleProvider())
            parameters.init(ECGenParameterSpec("secp256k1"))
            return parameters.getParameterSpec(ECParameterSpec::class.java)
        }
    }

    fun ByteArray.toHexString(): String {
        val builder = StringBuilder()
        val it = this.iterator()
        builder.append("0x")
        while (it.hasNext()) {
            builder.append(String.format("%02X ", it.next()))
        }

        return builder.toString()
    }
}