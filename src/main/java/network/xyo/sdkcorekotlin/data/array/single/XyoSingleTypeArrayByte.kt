package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder

open class XyoSingleTypeArrayByte(override val elementMajor : Byte,
                                  override val elementMinor : Byte,
                                  override var array: Array<XyoObject>) : XyoSingleTypeArrayBase() {

    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(XyoSingleTypeArrayShort.major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    override val typedId: ByteArray?
        get() = byteArrayOf(elementMajor, elementMinor)

    companion object : XyoArrayProvider() {
        override val minor: Byte = 0x01
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(1)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(byteArray[0].toInt())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, true, 1)
            val majorTypeValue = unpackedArray.majorType ?: return XyoResult(XyoError("Cant find major!"))
            val minorTypeValue = unpackedArray.minorType ?: return XyoResult(XyoError("Cant find minor!"))
            val unpackedArrayObject = XyoSingleTypeArrayByte(majorTypeValue, minorTypeValue, unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}