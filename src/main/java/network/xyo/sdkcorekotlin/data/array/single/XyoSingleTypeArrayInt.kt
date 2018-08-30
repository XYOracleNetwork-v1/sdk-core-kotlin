package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayUnpacker
import java.nio.ByteBuffer

open class XyoSingleTypeArrayInt(override val elementMajor : Byte,
                                 override val elementMinor : Byte,
                                 override var array: Array<XyoObject>) : XyoSingleTypeArrayBase() {

    override val typedId: ByteArray?
        get() = byteArrayOf(elementMajor, elementMinor)

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(XyoSingleTypeArrayShort.major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = sizeOfBytesToGetSize

    companion object : XyoArrayCreator() {
        override val minor: Byte
            get() = 0x03

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).int)
        }

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(4)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayUnpacker(byteArray, true, 4)
            val majorTypeValue = unpackedArray.majorType ?: return XyoResult(XyoError("Cant find major!"))
            val minorTypeValue = unpackedArray.minorType ?: return XyoResult(XyoError("Cant find minor!"))
            val unpackedArrayObject = XyoSingleTypeArrayInt(majorTypeValue, minorTypeValue, unpackedArray.array.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}