package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.rsa.XyoGeneralRsa.Companion.RSA_PUBLIC_EXPONENT
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.math.BigInteger
import java.security.interfaces.RSAPublicKey

/**
 * An Xyo Encoded RSA Public key.
 */
@ExperimentalStdlibApi
class XyoRsaPublicKey(private val modulus : BigInteger) : RSAPublicKey, XyoPublicKey(byteArrayOf(), 0) {
    private val publicExponent : BigInteger = BigInteger(RSA_PUBLIC_EXPONENT)

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getEncoded(): ByteArray {
        return getModulus().toByteArray()
    }

    override fun getFormat(): String {
        return "XyoRsaPublicKey"
    }

    override fun getModulus(): BigInteger {
        return modulus
    }

    override fun getPublicExponent(): BigInteger {
        return publicExponent
    }

    override var bytes: ByteArray = byteArrayOf()
        get() = XyoObjectStructure.newInstance(XyoSchemas.RSA_PUBLIC_KEY, encoded).bytesCopy


    companion object : XyoInterpret {
        override fun getInstance(byteArray: ByteArray): XyoRsaPublicKey {
            val value = XyoObjectStructure(byteArray, 0).valueCopy
            return XyoRsaPublicKey(BigInteger(value))
        }
    }
}