package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.signing.XyoPublicKey
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.nio.Buffer
import java.nio.ByteBuffer
import java.security.interfaces.RSAPublicKey

/**
 * An Xyo Encoded Rsa Public key.
 *
 * @major 0x04
 * @minor 0x03
 *
 * @param modulus the modulus of the public key.
 */
class XyoRsaPublicKey(private val modulus : BigInteger) : RSAPublicKey, XyoPublicKey {
    /**
     * @note All Xyo Rsa Public Key operations use the modulus 0x0100001
     */
    private val publicExponent : BigInteger = BigInteger(byteArrayOf(0x01, 0x00, 0x01))


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

    @ExperimentalUnsignedTypes
    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, encoded)

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.RSA_PUBLIC_KEY

    companion object : XyoFromSelf {

        @ExperimentalUnsignedTypes
        override fun getInstance(byteArray: ByteArray): XyoInterpreter {
            val value = XyoObjectCreator.getObjectValue(byteArray)
            return  XyoRsaPublicKey(BigInteger(value))
        }
    }
}