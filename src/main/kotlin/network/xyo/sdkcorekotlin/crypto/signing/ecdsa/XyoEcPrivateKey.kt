package network.xyo.sdkcorekotlin.crypto.signing.ecdsa

import network.xyo.sdkcorekotlin.crypto.signing.XyoPrivateKey
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.spec.ECParameterSpec
import java.math.BigInteger


abstract class XyoEcPrivateKey : ECPrivateKey, XyoPrivateKey() {

    override fun getAlgorithm(): String {
        return "ECDSA"
    }

    override fun getEncoded(): ByteArray {
        return d.toByteArray()
    }

    override fun getFormat(): String {
        return "XyoEcPrivateKey"
    }

    override val allowedOffset: Int
        get() = 0

    override var item: ByteArray = byteArrayOf()
        get() = XyoBuff.newInstance(XyoSchemas.EC_PRIVATE_KEY, d.toByteArray()).bytesCopy

    companion object {

        fun getInstance(byteArray: ByteArray, ecSpec: ECParameterSpec): XyoEcPrivateKey {
            return object : XyoEcPrivateKey() {
                override var item: ByteArray = byteArray

                override fun getParameters(): ECParameterSpec {
                    return ecSpec
                }

                override fun getD(): BigInteger {
                    return BigInteger(valueCopy)
                }
            }
        }

        fun getInstanceFromQ (q : BigInteger, ecSpec: ECParameterSpec): XyoEcPrivateKey {
            return object : XyoEcPrivateKey() {
                override fun getD(): BigInteger {
                    return q
                }

                override fun getParameters(): ECParameterSpec {
                    return ecSpec
                }
            }
        }
    }
}