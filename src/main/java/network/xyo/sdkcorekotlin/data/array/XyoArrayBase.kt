package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
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

    override val objectInBytes: ByteArray
        get() = makeArray()

    private fun makeArray () : ByteArray {
        if (typedId == null) {
            val merger = XyoByteArraySetter(array.size)
            for (i in 0..array.size - 1) {
                merger.add(array[i].typed, i)
            }
            return merger.merge()
        } else {
            val merger = XyoByteArraySetter(array.size + 1)
            val typeIdValue = typedId ?: throw Exception("Type is null!")
            merger.add(typeIdValue, 0)
            for (i in 0..array.size - 1) {
                merger.add(array[i].untyped, i + 1)
            }
            return merger.merge()
        }
    }

    /**
     * A base class for array providers.
     */
    abstract class XyoArrayProvider : XyoObjectProvider() {
        override val major: Byte = 0x01
    }
}