package network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoGeneralRsa.Companion.RSA_PUBLIC_EXPONENT
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.security.interfaces.RSAPublicKey

/**
 * An Xyo Encoded Rsa Public key.
 */
class XyoRsaPublicKey(private val modulus : BigInteger) : RSAPublicKey, XyoPublicKey {
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

    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, encoded)

    override val schema: XyoObjectSchema
        get() = XyoSchemas.RSA_PUBLIC_KEY

    companion object : XyoFromSelf {
        override fun getInstance(byteArray: ByteArray): XyoRsaPublicKey {
            val value = XyoObjectCreator.getObjectValue(byteArray)
            return XyoRsaPublicKey(BigInteger(value))
        }
    }
}