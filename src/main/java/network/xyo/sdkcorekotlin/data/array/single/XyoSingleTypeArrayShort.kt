package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import java.nio.ByteBuffer

/**
 * An single type array with a 2 byte size.
 *
 * @major 0x01
 * @minor 0x02
 *
 * @param elementMajor The major type of the elements in the array.
 * @param elementMinor The minor type of the elements in the array.
 * @param array The in-memory array to start off the Xyo array with.
 */
open class XyoSingleTypeArrayShort(override val elementMajor : Byte,
                                   override val elementMinor : Byte,
                                   override var array: Array<XyoObject>) : XyoSingleTypeArrayBase() {

    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    override val typedId: ByteArray?
        get() = byteArrayOf(elementMajor, elementMinor)


    companion object : XyoArrayProvider() {
        override val minor: Byte = 0x02
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(XyoUnsignedHelper.readUnsignedShort(byteArray))
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, true, 2)
            val majorTypeValue = unpackedArray.majorType ?: return XyoResult(XyoError(
                    this.toString(), "Cant find major!")
            )
            val minorTypeValue = unpackedArray.minorType ?: return XyoResult(XyoError(
                    this.toString(), "Cant find minor!")
            )
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
            val unpackedArrayObject = XyoSingleTypeArrayShort(majorTypeValue, minorTypeValue, unpackedArrayValue.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}