package network.xyo.sdkcorekotlin.data.array.multi

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder

/**
 * The base class for multi typed arrays.
 */
abstract class XyoMultiTypeArrayBase : XyoArrayBase() {
    override val typedId: ByteArray?
        get() = null

    abstract class XyoMultiTypeArrayBaseCreator : XyoArrayProvider() {

        abstract fun newInstance (array : Array<XyoObject>) : XyoMultiTypeArrayBase

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return newInstance(XyoArrayDecoder(byteArray, false, sizeOfBytesToGetSize ?: 0).array.toTypedArray())
        }
    }
}