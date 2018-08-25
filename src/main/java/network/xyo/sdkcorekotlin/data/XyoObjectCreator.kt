package network.xyo.sdkcorekotlin.data

abstract class XyoObjectCreator : XyoType() {
    abstract val defaultSize : Int?
    abstract val sizeOfSize : Int?
    abstract fun createFromPacked (byteArray: ByteArray) : XyoObject

    fun enable () {
        val minorMap = creators[major]
        if (minorMap == null) {
            creators[major] = HashMap()
            val newMinorMap = creators[major]!!
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

        fun create(data : ByteArray) : XyoObject? {
            val majorMap = creators[data[0]]
            if (majorMap != null) {
                return majorMap[data[1]]?.createFromPacked(XyoByteArrayReader(data).read(2, data.size - 2))
            }
            return null
        }

        fun getCreator (major: Byte, minor: Byte) : XyoObjectCreator? {
            val majorMap = creators[major]
            if (majorMap != null) {
                return majorMap[minor]
            }
            return null
        }

        fun getObjectContents (byteArray: ByteArray, includeSize: Boolean) : ByteArray {
            val reader = XyoByteArrayReader(byteArray)

            if (includeSize) {
                return reader.read(2, byteArray.size - 2)
            } else {
                var sizeOfSizeElement = XyoObjectCreator.getCreator(byteArray[0], byteArray[1])?.sizeOfSize

                if (sizeOfSizeElement == null) {
                    sizeOfSizeElement = XyoObjectCreator.getCreator(byteArray[0], byteArray[1])?.defaultSize
                }

                if (sizeOfSizeElement != null) {
                    return reader.read(sizeOfSizeElement, byteArray.size - 2)
                }
            }

            throw Exception()
        }
    }
}