package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import java.nio.ByteBuffer

/**
 * An array of origin blocks hashes that are being transferred.
 *
 * @major 0x02
 * @minor 0x16
 * @param array The array of origin block hashes.
 */
open class XyoBridgeHashSet(override var array: Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    companion object : XyoArrayProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x16
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(4)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(XyoUnsignedHelper.readUnsignedInt(byteArray))
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, false, 4).array
            if (unpackedArray.error != null) return XyoResult(
                    unpackedArray.error ?: XyoError(
                            this.toString(),
                            "Unknown array unpacking error!"
                    )
            )
            val unpackedArrayValue = unpackedArray.value ?: return XyoResult(XyoError(
                    this.toString(),
                    "Array value is null!"
            ))

            val unpackedArrayObject = XyoBridgeHashSet(unpackedArrayValue.toTypedArray())

            return XyoResult(unpackedArrayObject)
        }
    }
}