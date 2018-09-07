package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

abstract class XyoArrayBase : XyoObject() {
    abstract val typedId : ByteArray?
    abstract var array : Array<XyoObject>

    val size : Int
        get() = array.size

    open fun getElement (index : Int) : XyoObject? {
        return array[index]
    }

    open fun addElement (element : XyoObject, index: Int) {
        updateObjectCache()
        array[index] = element
    }

    override val objectInBytes: XyoResult<ByteArray>
        get() = makeArray()

    private fun makeArray () : XyoResult<ByteArray> {
        if (typedId == null) {
            val merger = XyoByteArraySetter(array.size)
            for (i in 0..array.size - 1) {
                val element = array[i].typed
                if (element.error != null) return XyoResult(
                        element.error ?: XyoError(
                                this.toString(),
                                "Unknown element packing error!"
                        )
                )
                val elementValue = element.value ?: return XyoResult(
                        XyoError(this.toString(), "Element payload is null!")
                )
                merger.add(elementValue, i)
            }
            return XyoResult(merger.merge())
        } else {
            val merger = XyoByteArraySetter(array.size + 1)
            val typedIdValue = typedId ?: return XyoResult(XyoError(
                    this.toString(),
                    "Type not found!")
            )
            merger.add(typedIdValue, 0)
            for (i in 0..array.size - 1) {
                val element = array[i].untyped
                if (element.error != null) return XyoResult(
                        element.error ?: XyoError(
                                this.toString(),
                                "Unknown element packing error!"
                        )
                )
                val elementValue = element.value ?: return XyoResult(
                        XyoError(this.toString(), "Element payload is null!")
                )
                merger.add(elementValue, i + 1)
            }
            return XyoResult(merger.merge())
        }
    }

    abstract class XyoArrayProvider : XyoObjectProvider() {
        override val major: Byte = 0x01
    }
}