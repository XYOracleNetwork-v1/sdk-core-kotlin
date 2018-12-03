package network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.RSAPrivateKey
import kotlin.experimental.and

/**
 * A Xyo Encoded RSA Private key.
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

    override val schema: XyoObjectSchema
        get() = XyoSchemas.RSA_PRIVATE_KEY

    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, encoded)

    companion object : XyoFromSelf {

        override fun getInstance(byteArray: ByteArray): XyoRsaPrivateKey {
            val value = XyoObjectCreator.getObjectValue(byteArray)
            val sizeOfPrivateExponent = value[0].toInt() and 0xff
            val privateExponent = value.copyOfRange(1, sizeOfPrivateExponent)
            val modulus = value.copyOfRange((sizeOfPrivateExponent), value.size)

            return XyoRsaPrivateKey(BigInteger(modulus), BigInteger(privateExponent))
        }
    }
}