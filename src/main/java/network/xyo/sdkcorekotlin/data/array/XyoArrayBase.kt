package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter

abstract class XyoArrayBase : XyoObject() {
    abstract val typedId : ByteArray?
    abstract val arraySize : ByteArray
    var array = ArrayList<XyoObject>()

    val size : Int
        get() = array.size

    open fun getElement (index : Int) : XyoObject? {
        return array[index]
    }

    open fun addElement (element : XyoObject, index: Int) {
        array.add(index, element)
    }

    open fun addElement (element: XyoObject) {
        array.add(element)
    }

    open fun removeElement(element: XyoObject) {
        array.remove(element)
    }

    open fun removeElementAtIndex(index : Int) {
        array.removeAt(index)
    }

    open fun removeAll() {
        array.clear()
    }

    override val data: ByteArray
        get() = makeArray()

    private fun makeArray () : ByteArray {
        if (typedId == null) {
            val merger = XyoByteArraySetter(array.size + 1)
            merger.add(arraySize, 0)
            for (i in 0..array.size - 1) {
                merger.add(array[i].typed, i + 1)
            }
            return merger.merge()
        } else {
            val merger = XyoByteArraySetter(array.size + 2)
            merger.add(typedId!!, 0)
            merger.add(arraySize, 1)
            for (i in 0..array.size - 1) {
                merger.add(array[i].untyped, i + 2)
            }
            return merger.merge()
        }
    }
}