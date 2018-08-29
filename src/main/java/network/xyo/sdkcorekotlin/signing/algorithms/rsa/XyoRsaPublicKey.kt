package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoResult
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

    override val data: XyoResult<ByteArray>
        get() = XyoResult(encoded)

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult<Int?>(null)

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getEncoded(): ByteArray {
        return mModulus.toByteArray()
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

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x04

        override val minor: Byte
            get() = 0x03

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).short.toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val reader = XyoByteArrayReader(byteArray)
            val modulusSize = ByteBuffer.allocate(4).put(reader.read(0, 4)).short.toInt() - 4
            val modulus = reader.read(0, modulusSize)

            return  XyoResult(XyoRsaPublicKey(BigInteger(modulus)))
        }
    }
}