package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import java.math.BigInteger
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint

class XyoCompressedEcPublicKey (x : BigInteger, y : BigInteger, ecSpec : ECParameterSpec) : ECPublicKey {
    private val mX = x
    private val mY = y
    private val mEcSpec = ecSpec

    override fun getAlgorithm(): String {
        return "EC"
    }

    override fun getEncoded(): ByteArray {
        val uncompressedEcPublicKey = XyoByteArraySetter(2)
        uncompressedEcPublicKey.add(mY.toByteArray(), 0)
        uncompressedEcPublicKey.add(mX.toByteArray(), 1)
        return uncompressedEcPublicKey.merge()
    }

    override fun getFormat(): String {
        return "XyoCompressedEcPublicKey"
    }

    override fun getParams(): ECParameterSpec {
        return mEcSpec
    }

    override fun getW(): ECPoint {
        return ECPoint(mX, mY)
    }
}