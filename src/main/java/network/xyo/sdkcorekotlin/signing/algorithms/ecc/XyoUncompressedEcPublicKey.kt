package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.signing.XyoPublicKey
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint

/**
 * A base class for all uncompressed EC public keys.
 */
@ExperimentalUnsignedTypes
abstract class XyoUncompressedEcPublicKey : ECPublicKey, XyoPublicKey {
    /**
     * The Java ECParameterSpec to understand the public key (x and y).
     */
    abstract val ecSpec : ECParameterSpec

    /**
     * The X point of the public key.
     */
    open val x : BigInteger
        get() = BigInteger(1, XyoObjectCreator.getObjectValue(self).copyOfRange(0, 32))

    /**
     * The Y point of the public key.
     */
    open val y : BigInteger
        get() = BigInteger(1, XyoObjectCreator.getObjectValue(self).copyOfRange(32, 64))

    override fun getAlgorithm(): String {
        return "EC"
    }

    override val schema: XyoObjectSchema
        get() = XyoSchemas.EC_PUBLIC_KEY

    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, encoded)

    override fun getEncoded(): ByteArray {
        val buffer = ByteBuffer.allocate(64)
        buffer.put(get32ByteEcPoint(x))
        buffer.put(get32ByteEcPoint(y))
        return buffer.array()
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


    private fun get32ByteEcPoint(point : BigInteger) : ByteArray {
        val encodedPoint = point.toByteArray()
        if (encodedPoint.size == 32) {
            return encodedPoint
        } else if (encodedPoint.size < 32) {
            val biggerPoint = ByteArray(32)
            val difference = biggerPoint.size - encodedPoint.size

            for (i in 0 until biggerPoint.size) {
                if (i > difference - 1) {
                    biggerPoint[i] = encodedPoint[i - difference]
                }
            }

            return biggerPoint
        }
        return encodedPoint.copyOfRange(1, 33)
    }

    /**
     * A base class for creating uncompressed EC public keys.
     */
    abstract class XyoUncompressedEcPublicKeyProvider : XyoFromSelf {
        /**
         * The Java ECParameterSpec to understand the public key (x and y).
         */
        abstract val ecPramSpec : ECParameterSpec

        @ExperimentalUnsignedTypes
        override fun getInstance(byteArray: ByteArray): XyoInterpreter {
            return object : XyoUncompressedEcPublicKey() {
                override val ecSpec: ECParameterSpec = ecPramSpec
                override val self: ByteArray
                    get() = byteArray
            }

        }
    }
}