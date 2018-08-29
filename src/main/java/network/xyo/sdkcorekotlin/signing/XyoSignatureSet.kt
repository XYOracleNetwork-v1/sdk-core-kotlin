package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayBase
import java.nio.ByteBuffer

open class XyoSignatureSet(override var array: Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x03

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).short.toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 2)
            val unpackedArrayObject = XyoSignatureSet(unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}