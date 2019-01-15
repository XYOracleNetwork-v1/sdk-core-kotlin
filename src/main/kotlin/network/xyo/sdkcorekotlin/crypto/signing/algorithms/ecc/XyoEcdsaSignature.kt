package network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * A base class for all EC signature operations.
 */
open class XyoEcdsaSignature(val r : BigInteger, val s : BigInteger) : XyoBuff() {

    override var item: ByteArray = XyoBuff.getObjectEncoded(XyoSchemas.EC_SIGNATURE, encode())

    override val allowedOffset: Int
        get() = 0

    private fun encode () : ByteArray {
        val encodedR = r.toByteArray()
        val encodedS = s.toByteArray()
        val buffer = ByteBuffer.allocate(2 + encodedR.size + encodedS.size)
        buffer.put(encodedR.size.toByte())
        buffer.put(encodedR)
        buffer.put(encodedS.size.toByte())
        buffer.put(encodedS)
        return buffer.array()
    }

    companion object : XyoInterpret {
        class XyoRAndS(val r : BigInteger, val s : BigInteger)

        protected fun getRAndS(byteArray: ByteArray) : XyoRAndS {
            val sizeOfR = byteArray[0].toInt()
            val sizeOfS = (byteArray[sizeOfR + 1]).toInt()
            val r = BigInteger(1, byteArray.copyOfRange(1, sizeOfR + 1))
            val s = BigInteger(1, byteArray.copyOfRange(sizeOfR + 2, sizeOfS + sizeOfR + 2))

            return XyoRAndS(r, s)
        }

        override fun getInstance(byteArray: ByteArray): XyoEcdsaSignature {
            val rAndS = getRAndS(object : XyoBuff() {
                override val allowedOffset: Int = 0
                override var item: ByteArray = byteArray
            }.valueCopy)
            return object : XyoEcdsaSignature(rAndS.r, rAndS.s) {
                override var item: ByteArray = byteArray
            }
        }
    }
}
