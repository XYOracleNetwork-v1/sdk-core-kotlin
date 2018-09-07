package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import java.nio.ByteBuffer

/**
 * An single type array with a 4 byte size.
 *
 * @major 0x01
 * @minor 0x06
 *
 * @param array The in-memory array to start off the Xyo array with.
 */
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
            if (unpackedArray.array.error != null) return XyoResult(
                    unpackedArray.array.error ?: XyoError(
                            this.toString(),
                            "Unknown array unpacking error!"
                    )
            )
            val unpackedArrayValue = unpackedArray.array.value ?: return XyoResult(XyoError(
                    this.toString(),
                    "Array value is null!"
            ))
            val unpackedArrayObject = XyoMultiTypeArrayInt(unpackedArrayValue.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}

