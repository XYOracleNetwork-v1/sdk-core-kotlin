package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.signing.XyoSignature
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature
import java.nio.ByteBuffer

abstract class XyoRsaSignature (rawSignature: ByteArray) : XyoSignature() {
    private val mSignature = rawSignature

    override val data: XyoResult<ByteArray>
        get() = XyoResult(mSignature)

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult(2)

    override val encodedSignature: ByteArray
        get() = mSignature

    abstract class XyoRsaSignatureCreator : XyoObjectCreator () {
        override val major: Byte
            get() = 0x05

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(2)

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