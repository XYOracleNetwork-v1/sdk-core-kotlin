package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoMultiTypeArrayInt(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(XyoMultiTypeArrayByte.major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x06

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(4)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).int)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayUnpacker(byteArray, false, 4)
            val unpackedArrayObject = XyoMultiTypeArrayInt(unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}

