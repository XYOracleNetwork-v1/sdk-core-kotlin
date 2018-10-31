package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.signing.XyoSignature
import java.math.BigInteger

/**
 * A base class for all EC signature operations.
 *
 * @param rawSignature the encoded EC signature.
 */
abstract class XyoEcdsaSignature(private val r : BigInteger, private val s : BigInteger) : XyoSignature() {
    override val objectInBytes: ByteArray
        get() = encodedSignature

    override val sizeIdentifierSize: Int? = 1

    override val encodedSignature: ByteArray
        get() = encode()

    private fun encode () : ByteArray {
        val encodedR = r.toByteArray()
        val encodedS = s.toByteArray()
        val merger = XyoByteArraySetter(4)
        merger.add(XyoUnsignedHelper.createUnsignedByte(encodedR.size), 0)
        merger.add(encodedR, 1)
        merger.add(XyoUnsignedHelper.createUnsignedByte(encodedS.size), 2)
        merger.add(encodedS, 3)
        return merger.merge()
    }

    abstract class XyoEcdsaSignatureProvider : XyoObjectProvider() {
        class XyoRAndS(val r : BigInteger, val s : BigInteger)

        override val major: Byte = 0x05
        override val sizeOfBytesToGetSize: Int = 1

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedByte(byteArray)
        }

        protected fun getRAndS(byteArray: ByteArray) : XyoRAndS {
            val totalSize = XyoUnsignedHelper.readUnsignedByte(byteArray)
            val sizeOfR = XyoUnsignedHelper.readUnsignedByte(byteArrayOf(byteArray[1]))
            val sizeOfS = XyoUnsignedHelper.readUnsignedByte(byteArrayOf(byteArray[1 + sizeOfR + 1]))
            val r = BigInteger(byteArray.copyOfRange(2, sizeOfR + 2))
            val s = BigInteger(byteArray.copyOfRange(sizeOfR + 3, sizeOfS + sizeOfR + 3))

            return XyoRAndS(r, s)
        }
    }
}
