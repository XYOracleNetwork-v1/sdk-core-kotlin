package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import java.nio.ByteBuffer

open class XyoMultiTypeArrayInt(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(XyoMultiTypeArrayByte.major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    companion object : XyoArrayProvider() {
        override val minor: Byte = 0x06
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(4)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).int)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, false, 4)
            val unpackedArrayObject = XyoMultiTypeArrayInt(unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}

