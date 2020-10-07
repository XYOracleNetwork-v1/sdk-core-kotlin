package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.crypto.signing.XyoPrivateKey
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.RSAPrivateKey

/**
 * An Xyo Encoded RSA Private key.
 */
@ExperimentalStdlibApi
open class XyoRsaPrivateKey : XyoPrivateKey, RSAPrivateKey {

    private val mod : BigInteger
    private val privateExp : BigInteger

    constructor(mod : BigInteger, privateExponent : BigInteger): super(byteArrayOf(), 0) {
        this.mod = mod
        this.privateExp = privateExponent
        this.bytes = XyoObjectStructure.newInstance(XyoSchemas.RSA_PRIVATE_KEY, encoded).bytesCopy
    }

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getEncoded(): ByteArray {
        val encodedPrivateExponent = privateExponent.toByteArray()
        val encodedModulus = modulus.toByteArray()

        val buffer = ByteBuffer.allocate(1 + encodedPrivateExponent.size + encodedModulus.size)
        buffer.put((encodedPrivateExponent.size + 1).toByte())
        buffer.put(encodedPrivateExponent)
        buffer.put(encodedModulus)

        return buffer.array()
    }

    override fun getFormat(): String {
        return "XyoRsaPrivateKey"
    }

    override fun getModulus(): BigInteger {
        return mod
    }

    override fun getPrivateExponent(): BigInteger {
        return privateExp
    }

    companion object : XyoInterpret {

        override fun getInstance(byteArray: ByteArray): XyoRsaPrivateKey {
            val value = XyoObjectStructure(byteArray, 0).valueCopy
            val sizeOfPrivateExponent = value[0].toInt() and 0xff
            val privateExponent = value.copyOfRange(1, sizeOfPrivateExponent)
            val modulus = value.copyOfRange((sizeOfPrivateExponent), value.size)

            return XyoRsaPrivateKey(BigInteger(modulus), BigInteger(privateExponent))
        }
    }
}