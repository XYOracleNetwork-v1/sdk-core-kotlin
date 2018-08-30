package network.xyo.sdkcorekotlin.data

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult

abstract class XyoObjectCreator : XyoType() {
    abstract val sizeOfBytesToGetSize : XyoResult<Int?>
    abstract fun readSize (byteArray: ByteArray) : XyoResult<Int>
    abstract fun createFromPacked (byteArray: ByteArray) : XyoResult<XyoObject>

    fun enable () {
        val minorMap = creators[major]
        if (minorMap == null) {
            creators[major] = HashMap()
            val newMinorMap = creators[major] ?: return
            newMinorMap[minor] = this
        } else {
            minorMap[minor] = this
        }
    }

    fun disable (major : Byte, minor: Byte) {
        val minorMap = creators[major]
        if (minorMap != null) {
            minorMap.remove(minor)
        }
    }

    companion object {
        private val creators = HashMap<Byte, HashMap<Byte, XyoObjectCreator>>()

        fun create(data : ByteArray) : XyoResult<XyoObject> {
            val majorMap = creators[data[0]]
            if (majorMap != null) {
                val creator = majorMap[data[1]]?.createFromPacked(XyoByteArrayReader(data).read(2, data.size - 2))
                if (creator != null) {
                    return creator
                }
                return XyoResult(XyoError(""))
            }
            return XyoResult(XyoError(""))
        }

        fun getCreator (major: Byte, minor: Byte) : XyoResult<XyoObjectCreator?> {
            val majorMap = creators[major]
            if (majorMap != null) {
                return XyoResult(majorMap[minor])
            }
            return XyoResult(XyoError(""))
        }
    }
}