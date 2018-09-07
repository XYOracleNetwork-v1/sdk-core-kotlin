package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import java.math.BigInteger
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint

/**
 * A compressed EC public key.
 *
 * @param x the x value of the public key.
 * @param y the y value of the public key.
 * @param ecSpec the Java ECParameterSpec to understand X and Y.
 */
class XyoCompressedEcPublicKey (private val x : BigInteger,
                                private val y : BigInteger,
                                private val ecSpec : ECParameterSpec) : ECPublicKey {

    override fun getAlgorithm(): String {
        return "EC"
    }

    override fun getEncoded(): ByteArray {
        val uncompressedEcPublicKey = XyoByteArraySetter(2)
        uncompressedEcPublicKey.add(y.toByteArray(), 0)
        uncompressedEcPublicKey.add(x.toByteArray(), 1)
        return uncompressedEcPublicKey.merge()
    }

    override fun getFormat(): String {
        return "XyoCompressedEcPublicKey"
    }

    override fun getParams(): ECParameterSpec {
        return ecSpec
    }

    override fun getW(): ECPoint {
        return ECPoint(x, y)
    }
}