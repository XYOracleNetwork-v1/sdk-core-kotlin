package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase

abstract class XyoSingleTypeArrayBase : XyoArrayBase() {
    abstract val elementMajor : Byte
    abstract val elementMinor : Byte

    override fun addElement(element: XyoObject) {
        if (element.id[0] == elementMajor && element.id[1] == elementMinor) {
            super.addElement(element)
        }
    }

    override fun addElement(element: XyoObject, index: Int) {
        if (element.id[0] == elementMajor && element.id[1] == elementMinor) {
            super.addElement(element, index)
        }
    }
}