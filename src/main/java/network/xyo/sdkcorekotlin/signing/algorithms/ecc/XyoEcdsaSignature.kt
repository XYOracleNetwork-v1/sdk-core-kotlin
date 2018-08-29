package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.signing.XyoSignature

abstract class XyoEcdsaSignature(rawSignature : ByteArray) : XyoSignature() {
    private val signature : ByteArray = rawSignature

    override val data: XyoResult<ByteArray>
        get() = XyoResult(signature)

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult(1)

    override val encodedSignature: ByteArray
        get() = signature

    abstract class XyoEcdsaSignatureCreator : XyoObjectCreator() {
        override val major: Byte
            get() = 0x05

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(1)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(byteArray[0].toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val size = byteArray[0].toInt()

            return XyoResult(object : XyoEcdsaSignature(XyoByteArrayReader(byteArray).read(1, size - 1)) {
                override val id: XyoResult<ByteArray>
                    get() = XyoResult(byteArrayOf(major, minor))
            })
        }
    }
}
