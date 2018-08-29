package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoMultiTypeArrayShort(override var array: Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(XyoMultiTypeArrayByte.major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x05

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).short.toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 2)
            val unpackedArrayObject = XyoMultiTypeArrayShort(unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}