package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase

abstract class XyoSingleTypeArrayBase : XyoArrayBase() {
    abstract val elementMajor : Byte
    abstract val elementMinor : Byte

    override fun addElement(element: XyoObject, index: Int) {
        val elementId = element.id.value
        if (elementId != null) {
            if (elementId[0] == elementMajor && elementId[0] == elementMinor) {
                super.addElement(element, index)
            }
        }
    }
}