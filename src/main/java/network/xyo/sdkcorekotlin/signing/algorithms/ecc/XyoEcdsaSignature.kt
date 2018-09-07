package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoResult
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

    override val objectInBytes: XyoResult<ByteArray>
        get() = XyoResult(signature)

    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult(1)

    override val encodedSignature: ByteArray
        get() = signature

    abstract class XyoEcdsaSignatureProvider : XyoObjectProvider() {
        override val major: Byte = 0x05
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(1)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(XyoUnsignedHelper.readUnsignedByte(byteArray))
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val size = XyoUnsignedHelper.readUnsignedByte(byteArray)

            return XyoResult(object : XyoEcdsaSignature(XyoByteArrayReader(byteArray).read(1, size - 1)) {
                override val id: XyoResult<ByteArray>
                    get() = XyoResult(byteArrayOf(major, minor))
            })
        }
    }
}
