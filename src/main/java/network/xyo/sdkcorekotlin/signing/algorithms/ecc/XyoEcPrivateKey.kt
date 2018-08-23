package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import java.math.BigInteger
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECParameterSpec

class XyoEcPrivateKey(s : BigInteger, ecSpec: ECParameterSpec) : ECPrivateKey {
    private val mS = s
    private val mEcSpec = ecSpec

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
        return  mEcSpec
    }

    override fun getS(): BigInteger {
        return mS
    }
}