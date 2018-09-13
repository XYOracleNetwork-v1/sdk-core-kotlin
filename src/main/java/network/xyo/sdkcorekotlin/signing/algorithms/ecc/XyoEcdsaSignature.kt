package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.signing.XyoSignature

/**
 * A base class for all EC signature operations.
 *
 * @param rawSignature the encoded EC signature.
 */
abstract class XyoEcdsaSignature(rawSignature : ByteArray) : XyoSignature() {
    private val signature : ByteArray = rawSignature

    override val objectInBytes: ByteArray
        get() = signature

    override val sizeIdentifierSize: Int? = 1

    override val encodedSignature: ByteArray
        get() = signature

    abstract class XyoEcdsaSignatureProvider : XyoObjectProvider() {
        override val major: Byte = 0x05
        override val sizeOfBytesToGetSize: Int = 1

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedByte(byteArray)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val size = XyoUnsignedHelper.readUnsignedByte(byteArray)

            return object : XyoEcdsaSignature(XyoByteArrayReader(byteArray).read(1, size - 1)) {
                override val id: ByteArray
                    get() = byteArrayOf(major, minor)
            }
        }
    }
}
