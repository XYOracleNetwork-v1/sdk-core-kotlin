package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import java.math.BigInteger
import java.security.interfaces.RSAPublicKey

/**
 * An Xyo Encoded Rsa Public key.
 *
 * @major 0x04
 * @minor 0x03
 *
 * @param modulus the modulus of the public key.
 */
class XyoRsaPublicKey(private val modulus : BigInteger) : RSAPublicKey, XyoObject() {
    /**
     * @note All Xyo Rsa Public Key operations use the modulus 0x0100001
     */
    private val publicExponent : BigInteger = BigInteger(byteArrayOf(0x01, 0x00, 0x01))

    override val objectInBytes: ByteArray
        get() = encoded

    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

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

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x04
        override val minor: Byte = 0x03
        override val sizeOfBytesToGetSize: Int? = 2

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedShort(byteArray)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val modulusSize = XyoUnsignedHelper.readUnsignedShort(reader.read(0, 2))
            val modulus = reader.read(2, modulusSize - 2)

            return  XyoRsaPublicKey(BigInteger(modulus))
        }
    }
}