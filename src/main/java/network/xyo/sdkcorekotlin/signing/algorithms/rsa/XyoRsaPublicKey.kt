package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.RSAPublicKey

class XyoRsaPublicKey(modulus : BigInteger) : RSAPublicKey, XyoObject() {
    private val mModulus : BigInteger = modulus
    private val mPublicExponent : BigInteger = BigInteger(byteArrayOf(0x01, 0x00, 0x01))

    override val data: ByteArray
        get() = encoded

    override val sizeIdentifierSize: Int?
        get() = null

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getEncoded(): ByteArray {
        val setter = XyoByteArraySetter(1)
        val byteModulus = mModulus.toByteArray()
        val reader = XyoByteArrayReader(byteModulus)
        setter.add(reader.read(1, byteModulus.size), 0)
        return setter.merge()
    }

    override fun getFormat(): String {
        return "XyoRsaPublicKey"
    }

    override fun getModulus(): BigInteger {
        return mModulus
    }

    override fun getPublicExponent(): BigInteger {
        return mPublicExponent
    }

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x04

        override val minor: Byte
            get() = 0x03

        override val sizeOfSize: Int?
            get() = 2

        override val defaultSize: Int?
            get() = null

        override fun createFromPacked(byteArray: ByteArray): XyoRsaPublicKey {
            val reader = XyoByteArrayReader(byteArray)
            val modulusSize = ByteBuffer.allocate(2).put(reader.read(2, 2)).getShort().toInt() - 2
            val modulus = reader.read(2, modulusSize)

            return  XyoRsaPublicKey(BigInteger(modulus))
        }
    }
}