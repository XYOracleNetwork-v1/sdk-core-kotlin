package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import java.math.BigInteger
import java.nio.ByteBuffer
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
        uncompressedEcPublicKey.add(x.toByteArray(), 0)
        uncompressedEcPublicKey.add(y.toByteArray(), 1)
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

    override val data: ByteArray
        get() = encoded

    override val sizeIdentifierSize: Int?
        get() = null

    abstract class XyoUncompressedEcPublicKeyCreator : XyoObjectCreator () {
        abstract val ecPramSpec : ECParameterSpec

        override val major: Byte
            get() = 0x04

        override val sizeOfBytesToGetSize: Int
            get() = 0

        override fun readSize(byteArray: ByteArray): Int {
            return 64
        }

        override fun createFromPacked(byteArray: ByteArray): XyoUncompressedEcPublicKey {
            val reader = XyoByteArrayReader(byteArray)
            val xPoint = BigInteger(reader.read(0, 32))
            val yPoint = BigInteger(reader.read(32, 32))

            return object : XyoUncompressedEcPublicKey() {
                override val ecSpec: ECParameterSpec
                    get() = ecPramSpec

                override val x: BigInteger
                    get() = xPoint

                override val y: BigInteger
                    get() = yPoint

                override val id: ByteArray
                    get() = byteArrayOf(major, minor)
            }
        }
    }
}