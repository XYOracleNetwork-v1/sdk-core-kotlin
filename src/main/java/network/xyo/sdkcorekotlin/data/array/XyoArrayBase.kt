package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt

abstract class XyoArrayBase : XyoObject() {
    abstract val typedId : ByteArray?
    abstract var array : Array<XyoObject>

    val size : Int
        get() = array.size

    open fun getElement (index : Int) : XyoObject? {
        return array[index]
    }

    open fun addElement (element : XyoObject, index: Int) {
        array[index] = element
    }

    override val data: ByteArray
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
            merger.add(typedId!!, 0)
            for (i in 0..array.size - 1) {
                merger.add(array[i].untyped, i + 1)
            }
            return merger.merge()
        }
    }

    abstract class XyoArrayCreator : XyoObjectCreator() {
        override val major: Byte
            get() = 0x01
    }
}