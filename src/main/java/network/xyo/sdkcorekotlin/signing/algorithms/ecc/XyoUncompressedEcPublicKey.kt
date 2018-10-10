package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import java.math.BigInteger
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint

/**
 * A base class for all uncompressed EC public keys.
 */
abstract class XyoUncompressedEcPublicKey : ECPublicKey, XyoObject() {
    /**
     * The Java ECParameterSpec to understand the public key (x and y).
     */
    abstract val ecSpec : ECParameterSpec

    /**
     * The X point of the public key.
     */
    abstract val x : BigInteger

    /**
     * The Y point of the public key.
     */
    abstract val y : BigInteger

    override fun getAlgorithm(): String {
        return "EC"
    }

    override fun getEncoded(): ByteArray {
        val uncompressedEcPublicKey = XyoByteArraySetter(2)
        uncompressedEcPublicKey.add(get32ByteEcPoint(x), 0)
        uncompressedEcPublicKey.add(get32ByteEcPoint(y), 1)
        return uncompressedEcPublicKey.merge()
    }

    override fun getFormat(): String {
        return "XyoUncompressedEcPublicKey"
    }

    override fun getParams(): ECParameterSpec {
        return ecSpec
    }

    override fun getW(): ECPoint {
        return ECPoint(x, y)
    }

    override val objectInBytes: ByteArray
        get() = encoded

    override val sizeIdentifierSize: Int?
        get() = null

    private fun get32ByteEcPoint(point : BigInteger) : ByteArray {
        val encodedPoint = point.toByteArray()
        if (encodedPoint.size == 32) {
            return encodedPoint
        }
        return encodedPoint.copyOfRange(1, 33)
    }

    /**
     * A base class for creating uncompressed EC public keys.
     */
    abstract class XyoUncompressedEcPublicKeyProvider : XyoObjectProvider () {
        /**
         * The Java ECParameterSpec to understand the public key (x and y).
         */
        abstract val ecPramSpec : ECParameterSpec
        override val major: Byte = 0x04
        override val sizeOfBytesToGetSize: Int? = 0

        override fun readSize(byteArray: ByteArray): Int {
            return 64
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val xPoint = BigInteger(reader.read(0, 32))
            val yPoint = BigInteger(reader.read(32, 32))

            return object : XyoUncompressedEcPublicKey() {
                override val ecSpec: ECParameterSpec = ecPramSpec
                override val x: BigInteger = xPoint
                override val y: BigInteger = yPoint
                override val id: ByteArray = byteArrayOf(major, minor)
            }
        }
    }
}