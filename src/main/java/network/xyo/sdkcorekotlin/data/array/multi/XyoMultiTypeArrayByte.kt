package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder

open class XyoMultiTypeArrayByte(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    companion object : XyoArrayProvider() {
        override val minor: Byte = 0x04
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(1)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(byteArray[0].toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, false, 1)
            val unpackedArrayObject = XyoMultiTypeArrayByte(unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}