package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import java.math.BigInteger
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECParameterSpec

class XyoEcPrivateKey(private val s : BigInteger,
                      private val ecSpec: ECParameterSpec) : ECPrivateKey {
    override fun getAlgorithm(): String {
        return "EC"
    }

    override fun getEncoded(): ByteArray {
        return s.toByteArray()
    }

    override fun getFormat(): String {
        return "XyoEcPrivateKey"
    }

    override fun getParams(): ECParameterSpec {
        return  ecSpec
    }

    override fun getS(): BigInteger {
        return s
    }
}