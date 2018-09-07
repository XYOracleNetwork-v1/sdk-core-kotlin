package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import java.math.BigInteger
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint

abstract class XyoUncompressedEcPublicKey : ECPublicKey, XyoObject() {
    abstract val ecSpec : ECParameterSpec
    abstract val x : BigInteger
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

    override val objectInBytes: XyoResult<ByteArray>
        get() = XyoResult(encoded)

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult<Int?>(null)

    fun get32ByteEcPoint(point : BigInteger) : ByteArray {
        val encodedPoint = point.toByteArray()
        if (encodedPoint.size == 32) {
            return encodedPoint
        }
        return encodedPoint.copyOfRange(1, 33)
    }

    abstract class XyoUncompressedEcPublicKeyProvider : XyoObjectProvider () {
        abstract val ecPramSpec : ECParameterSpec
        override val major: Byte = 0x04
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(0)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(64)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val reader = XyoByteArrayReader(byteArray)
            val xPoint = BigInteger(reader.read(0, 32))
            val yPoint = BigInteger(reader.read(32, 32))

            return XyoResult(object : XyoUncompressedEcPublicKey() {
                override val ecSpec: ECParameterSpec = ecPramSpec
                override val x: BigInteger = xPoint
                override val y: BigInteger = yPoint
                override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
            })
        }
    }
}