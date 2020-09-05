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
class XyoUncompressedEcPublicKey : ECPublicKey, XyoPublicKey {

    constructor(ecSpec: ECParameterSpec, bytes: ByteArray) :super(bytes) {
        this.ecSpec = ecSpec
        this.bytes = getObjectEncoded(XyoSchemas.EC_PUBLIC_KEY, encoded)
        this.x = BigInteger(1, valueCopy.copyOfRange(0, 32))
        this.y = BigInteger(1, valueCopy.copyOfRange(32, 64))
    }

    constructor(ecSpec: ECParameterSpec, x: BigInteger, y: BigInteger) :super() {
        this.ecSpec = ecSpec
        this.x = x
        this.y = y
    }

    /**
     * The Java ECParameterSpec to understand the public key (x and y).
     */
    val ecSpec : ECParameterSpec

    /**
     * The X point of the public key.
     */
    val x : BigInteger

    /**
     * The Y point of the public key.
     */
    val y : BigInteger

    override fun getAlgorithm(): String {
        return "EC"
    }

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