package network.xyo.sdkcorekotlin.data

/**
 * Can create a XYO Object from a ByteArray and stores information about that object.
 */
abstract class XyoObjectProvider : XyoType() {
    /**
     * The number of bytes to read to get the size.
     */
    abstract val sizeOfBytesToGetSize : Int?

    /**
     * Gets the size of the payload given a number (from sizeOfBytesToGetSize) of bytes
     * starting from the beginning of the entire encoded object. For example, if an object has
     * a 4 byte size identifier, it will expect 4 bytes to be passed to this function and will
     * return the size of the entire object.
     *
     * @param byteArray A group of bytes starting from the beginning of the entire object. The
     * size of this field is obtained from sizeOfBytesToGetSize
     * @return The size of the entire encoded payload.
     */
    abstract fun readSize (byteArray: ByteArray) : Int

    /**
     * Given the encoded object (obtained from getUntyped() from XyoObject) will return a XyoObject
     * of that type.
     *
     * @param byteArray A encoded object obtained from getUntyped() from a XyoObject.
     * @return The XyoObject that the provider is providing.
     */
    abstract fun createFromPacked (byteArray: ByteArray) : XyoObject

    /**
     * Adds the current object to a mapping from major and minor to the creator.
     */
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

    /**
     * Removes the current object from the mapping from major and minor to the creator.
     */
    fun disable (major : Byte, minor: Byte) {
        val minorMap = creators[major]
        if (minorMap != null) {
            minorMap.remove(minor)
        }
    }

    companion object {
        private val creators = HashMap<Byte, HashMap<Byte, XyoObjectProvider>>()

        /**
         * Creates a object from its typed form.
         *
         * @param data The encoded typed form of the object.
         * @return The created object wrapped in a XyoObject.
         */
        fun create(data : ByteArray) : XyoObject? {
            val majorMap = creators[data[0]]
            if (majorMap != null) {
                val creator = majorMap[data[1]]?.createFromPacked(XyoByteArrayReader(data).read(2, data.size - 2))
                if (creator != null) {
                    return creator
                }
            }
            return null
        }

        /**
         * Finds an object creator from its major and minor.
         *
         * @param major The object creator major.
         * @param minor The object creator minor.
         * @return the object creator.
         */
        fun getCreator (major: Byte, minor: Byte) : XyoObjectProvider? {
            val majorMap = creators[major]
            if (majorMap != null) {
                return majorMap[minor]
            }
            return null
        }

        /**
         * Decodes a group of objects.
         *
         * @param encodedArray The encoded objects to decode.
         */
        fun encodedToDecodedArray (encodedArray : Array<ByteArray>) : Array<XyoObject> {
            val toSend = ArrayList<XyoObject>()
            for (encoded in encodedArray) {
                val obj = XyoObjectProvider.create(encoded)
                if (obj != null) {
                    toSend.add(obj)
                }
            }
            return toSend.toTypedArray()
        }
    }
}