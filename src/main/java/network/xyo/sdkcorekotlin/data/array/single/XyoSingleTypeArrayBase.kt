package network.xyo.sdkcorekotlin.data.array.single

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayBase

/**
 * A base class for single typed arrays.
 */
abstract class XyoSingleTypeArrayBase : XyoArrayBase() {
    /**
     * The type of the elements in the array major.
     */
    abstract val elementMajor : Byte

    /**
     * The type of the elements in the array minor.
     */
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