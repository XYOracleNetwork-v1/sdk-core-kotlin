package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.RSAPrivateKey

/**
 * A Xyo Encoded RSA Private key.
 *
 * @major 0x0a
 * @minor 0x01
 */
open class XyoRsaPrivateKey (private val mod : BigInteger, private val privateExponent : BigInteger) : RSAPrivateKey, XyoInterpreter {

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getEncoded(): ByteArray {
        val encodedPrivateExponent = getPrivateExponent().toByteArray()
        val encodedModulus = modulus.toByteArray()

        val buffer = ByteBuffer.allocate(1 + encodedPrivateExponent.size + encodedModulus.size)
        buffer.put((encodedPrivateExponent.size + 1).toByte())
        buffer.put(encodedPrivateExponent)
        buffer.put(encodedModulus)

        return buffer.array()
    }

    override fun getFormat(): String {
        return "RSA"
    }

    override fun getModulus(): BigInteger {
        return mod
    }

    override fun getPrivateExponent(): BigInteger {
        return privateExponent
    }

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.RSA_PRIVATE_KEY

    @ExperimentalUnsignedTypes
    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, encoded)

    companion object : XyoFromSelf {

        @ExperimentalUnsignedTypes
        override fun getInstance(byteArray: ByteArray): XyoRsaPrivateKey {

            val sizeOfPrivateExponent = byteArray[2].toInt() - 1
            val privateExponent = XyoObjectCreator.getObjectValue(byteArray).copyOfRange(3, sizeOfPrivateExponent)
            val modulus = XyoObjectCreator.getObjectValue(byteArray).copyOfRange((3 + sizeOfPrivateExponent), byteArray.size - (3 + sizeOfPrivateExponent))

            return XyoRsaPrivateKey(BigInteger(modulus), BigInteger(privateExponent))
        }
    }
}