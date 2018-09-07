package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder

/**
 * An single type array with a 1 byte size.
 *
 * @major 0x01
 * @minor 0x04
 *
 * @param array The in-memory array to start off the Xyo array with.
 */
open class XyoMultiTypeArrayByte(override var array : Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    companion object : XyoArrayProvider() {
        override val minor: Byte = 0x04
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(1)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(XyoUnsignedHelper.readUnsignedByte(byteArray))
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, false, 1)
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
            val unpackedArrayObject = XyoMultiTypeArrayByte(unpackedArrayValue.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}