package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.data.*
import java.math.BigInteger
import java.security.interfaces.RSAPrivateKey
import java.security.spec.RSAPublicKeySpec

/**
 * A Xyo Encoded RSA Private key.
 *
 * @major 0x0a
 * @minor 0x01
 */
class XyoRsaPrivateKey (private val mod : BigInteger, private val privateExponent : BigInteger) : RSAPrivateKey, XyoObject() {

    override val id: ByteArray = byteArrayOf(major, minor)

    override val objectInBytes: ByteArray = encoded

    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getEncoded(): ByteArray {
        val encodedPrivateExponent = getPrivateExponent().toByteArray()
        val encodedModulus = modulus.toByteArray()

        val setter = XyoByteArraySetter(3)
        setter.add(XyoUnsignedHelper.createUnsignedByte(encodedPrivateExponent.size + 1), 0)
        setter.add(encodedPrivateExponent, 1)
        setter.add(encodedModulus, 2)

        return setter.merge()
    }

    override fun getFormat(): String {
        return "XYO"
    }

    override fun getModulus(): BigInteger {
        return mod
    }

    override fun getPrivateExponent(): BigInteger {
        return privateExponent
    }

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x0a

        override val minor: Byte = 0x01

        override val sizeOfBytesToGetSize: Int? = 2

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val sizeOfPrivateExponent = XyoUnsignedHelper.readUnsignedByte(byteArrayOf(byteArray[2])) - 1
            val privateExponent = reader.read(3, sizeOfPrivateExponent)
            val modulus = reader.read(3 + sizeOfPrivateExponent, byteArray.size - (3 + sizeOfPrivateExponent))

            return XyoRsaPrivateKey(BigInteger(modulus), BigInteger(privateExponent))
        }

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedShort(byteArray)
        }
    }
}