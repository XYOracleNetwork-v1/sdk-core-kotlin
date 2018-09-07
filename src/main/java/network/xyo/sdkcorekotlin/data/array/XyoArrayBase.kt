package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

/**
 * A base class for encoding Xyo arrays.
 */
abstract class XyoArrayBase : XyoObject() {
    /**
     * The type of the elements in the array. If this value it null, it is a multi element array.
     */
    abstract val typedId : ByteArray?

    /**
     * The in-memory array to be encoded.
     */
    abstract var array : Array<XyoObject>

    /**
     * The number of elements in the array.
     */
    val size : Int
        get() = array.size

    /**
     * Gets an element from the array.
     *
     * @param index The index of the element.
     * @return The element at the index.
     */
    open fun getElement (index : Int) : XyoObject? {
        return array[index]
    }

    /**
     * Adds a element to the array.
     *
     * @param element The element to add.
     * @param index The index to add it at.
     */
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

    /**
     * A base class for array providers.
     */
    abstract class XyoArrayProvider : XyoObjectProvider() {
        override val major: Byte = 0x01
    }
}