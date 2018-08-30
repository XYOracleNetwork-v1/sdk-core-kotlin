package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.signing.XyoSignature
import java.nio.ByteBuffer

abstract class XyoRsaSignature (private val signature: ByteArray) : XyoSignature() {
    override val data: XyoResult<ByteArray> = XyoResult(signature)
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult(2)
    override val encodedSignature: ByteArray = signature

    abstract class XyoRsaSignatureProvider : XyoObjectProvider () {
        override val major: Byte = 0x05

        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).short.toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val size = ByteBuffer.wrap(byteArray).short.toInt()

            return XyoResult(object : XyoRsaSignature(XyoByteArrayReader(byteArray).read(2, size - 2)) {
                override val id: XyoResult<ByteArray>
                    get() = XyoResult(byteArrayOf(major, minor))
            })
        }
    }
}