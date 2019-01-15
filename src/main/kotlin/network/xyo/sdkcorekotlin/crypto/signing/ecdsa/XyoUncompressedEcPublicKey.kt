package network.xyo.sdkcorekotlin.crypto.signing.ecdsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.nio.ByteBuffer


/**
 * A base class for all uncompressed EC public keys.
 */
abstract class XyoUncompressedEcPublicKey : ECPublicKey, XyoPublicKey() {
    /**
     * The Java ECParameterSpec to understand the public key (x and y).
     */
    abstract val ecSpec : ECParameterSpec

    /**
     * The X point of the public key.
     */
    open val x : BigInteger
        get() = BigInteger(1, valueCopy.copyOfRange(0, 32))

    /**
     * The Y point of the public key.
     */
    open val y : BigInteger
        get() = BigInteger(1, valueCopy.copyOfRange(32, 64))

    override fun getAlgorithm(): String {
        return "EC"
    }

    override var item: ByteArray = byteArrayOf()
        get() = XyoBuff.getObjectEncoded(XyoSchemas.EC_PUBLIC_KEY, encoded)

    override fun getEncoded(): ByteArray {
        val buffer = ByteBuffer.allocate(64)
        buffer.put(get32ByteEcPoint(x))
        buffer.put(get32ByteEcPoint(y))
        return buffer.array()
    }

    override fun getFormat(): String {
        return "XyoUncompressedEcPublicKey"
    }

    override fun getQ(): ECPoint {
        return ecSpec.curve.createPoint(x, y)
    }

    override fun getParameters(): ECParameterSpec {
        return ecSpec
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
    abstract class XyoUncompressedEcPublicKeyProvider : XyoInterpret {
        /**
         * The Java ECParameterSpec to understand the public key (x and y).
         */
        abstract val ecPramSpec : ECParameterSpec

        override fun getInstance(byteArray: ByteArray): XyoUncompressedEcPublicKey {
            return object : XyoUncompressedEcPublicKey() {
                override val ecSpec: ECParameterSpec = ecPramSpec
                override val allowedOffset: Int
                    get() = 0

                override var item: ByteArray = byteArray
            }

        }
    }
}