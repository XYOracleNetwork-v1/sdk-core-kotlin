package network.xyo.sdkcorekotlin.crypto.signing.ecdsa

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger
import java.nio.ByteBuffer


/**
 * A base class for all uncompressed EC public keys.
 */
abstract class XyoUncompressedEcPublicKey(byteArray: ByteArray? = null) : ECPublicKey, XyoPublicKey(byteArray ?: byteArrayOf(), 0) {
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

    override fun getItem() = getObjectEncoded(XyoSchemas.EC_PUBLIC_KEY, encoded)

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
        }
        
        return encodedPoint.copyOfRange(1, 33)
    }
}