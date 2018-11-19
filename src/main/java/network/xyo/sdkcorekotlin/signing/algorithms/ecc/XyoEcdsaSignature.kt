package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * A base class for all EC signature operations.
 *
 * @param rawSignature the encoded EC signature.
 */
open class XyoEcdsaSignature(val r : BigInteger, val s : BigInteger) : XyoInterpreter {

    @ExperimentalUnsignedTypes
    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, encode())

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

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema = XyoSchemas.EC_SIGNATURE

    companion object : XyoFromSelf {
        class XyoRAndS(val r : BigInteger, val s : BigInteger)

        protected fun getRAndS(byteArray: ByteArray) : XyoRAndS {
            val sizeOfR = byteArray[1].toInt()
            val sizeOfS = (byteArray[1 + sizeOfR + 1]).toInt()
            val r = BigInteger(byteArray.copyOfRange(2, sizeOfR + 2))
            val s = BigInteger(byteArray.copyOfRange(sizeOfR + 3, sizeOfS + sizeOfR + 3))

            return XyoRAndS(r, s)
        }

        @ExperimentalUnsignedTypes
        override fun getInstance(byteArray: ByteArray): XyoInterpreter {
            val rAndS = getRAndS(XyoObjectCreator.getObjectValue(byteArray))
            return object : XyoEcdsaSignature(rAndS.r, rAndS.s) {
                override val self: ByteArray
                    get() = byteArray
            }
        }
    }
}
