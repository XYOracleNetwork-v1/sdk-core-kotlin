package network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.crypto.signing.XyoPrivateKey
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.RSAPrivateKey

/**
 * A Xyo Encoded RSA Private key.
 */
open class XyoRsaPrivateKey (private val mod : BigInteger, private val privateExponent : BigInteger) : XyoPrivateKey(), RSAPrivateKey {

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

    override val allowedOffset: Int
        get() = 0

    override var item: ByteArray = byteArrayOf()
        get() = XyoBuff.newInstance(XyoSchemas.RSA_PRIVATE_KEY, encoded).bytesCopy

    companion object : XyoInterpret {

        override fun getInstance(byteArray: ByteArray): XyoRsaPrivateKey {
            val value = object : XyoBuff() {
                override val allowedOffset: Int
                    get() = 0

                override var item: ByteArray = byteArray
            }.valueCopy
            val sizeOfPrivateExponent = value[0].toInt() and 0xff
            val privateExponent = value.copyOfRange(1, sizeOfPrivateExponent)
            val modulus = value.copyOfRange((sizeOfPrivateExponent), value.size)

            return XyoRsaPrivateKey(BigInteger(modulus), BigInteger(privateExponent))
        }
    }
}