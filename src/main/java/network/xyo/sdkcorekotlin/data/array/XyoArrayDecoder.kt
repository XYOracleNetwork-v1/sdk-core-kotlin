package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException
import network.xyo.sdkcorekotlin.exceptions.XyoNoObjectException
import java.nio.ByteBuffer

/**
 * A class to decode standard Xyo Arrays from bytes.
 *
 * @param data The encoded array.
 * @param typed If the array is a typed array or not.
 * @param sizeOfSize The size of the size of the array.
 */
class XyoArrayDecoder (private val data : ByteArray,
                       private val typed: Boolean,
                       private val sizeOfSize: Int) : Iterator<XyoObject> {

    private var globalCurrentPosition = 0

    /**
     * The major type of the array
     */
    var majorType: Byte? = null

    /**
     * The minor type of the array
     */
    var minorType: Byte? = null

    /**
     * The unpacked array.
     */
    val array : ArrayList<XyoObject> = unpack()

    private fun getMajorMinor () : ByteArray {
        val major = data[globalCurrentPosition]
        val minor = data[globalCurrentPosition + 1]
        globalCurrentPosition += 2
        return byteArrayOf(major, minor)
    }

    private fun readCurrentSizeFromType (major: Byte, minor: Byte) : Int? {
        val typeObject = XyoObjectProvider.getCreator(major, minor)
        if (typeObject != null) {
            val sizeOfBytesToRead = typeObject.sizeOfBytesToGetSize ?: 0
            if (sizeOfBytesToRead + globalCurrentPosition > data.size) return null
            return typeObject.readSize(readBytes(sizeOfBytesToRead))
        }
        return null
    }

    override fun hasNext(): Boolean {
        return globalCurrentPosition < data.size
    }

    override fun next(): XyoObject {
        if (typed) {
            return getNextElement(byteArrayOf(majorType!!, minorType!!))
        }
        return getNextElement(getMajorMinor())
    }

    private fun unpack () : ArrayList<XyoObject> {
        val expectedSize = getSize(sizeOfSize)
        if (expectedSize != data.size) throw XyoCorruptDataException("Invalid size.")
        val items = ArrayList<XyoObject>()
        var arrayType : ByteArray = byteArrayOf()

        if (typed) {
            arrayType = getMajorMinor()
            majorType = arrayType[0]
            minorType = arrayType[1]
        }


        while (globalCurrentPosition < data.size) {
            if (!typed) {
                if (globalCurrentPosition + 2 < data.size) {
                    arrayType = getMajorMinor()
                } else {
                    throw XyoCorruptDataException("Array out of size!")
                }
            }

            items.add(getNextElement(arrayType))
        }

        return items
    }


    private fun getNextElement (arrayType : ByteArray) : XyoObject {
        val sizeOfElement = readCurrentSizeFromType(arrayType[0], arrayType[1])
        if (sizeOfElement != null) {
            val field = ByteArray(sizeOfElement)
            var position = 0

            if (globalCurrentPosition + (sizeOfElement - 1) < data.size) {
                for (i in globalCurrentPosition..globalCurrentPosition + (sizeOfElement - 1)) {
                    val byte = data[i]
                    field[position] = byte
                    position++
                }
            } else {
                throw XyoCorruptDataException("Array out of size!")
            }

            globalCurrentPosition += sizeOfElement

            val merger = XyoByteArraySetter(3)
            merger.add(byteArrayOf(arrayType[0]), 0)
            merger.add(byteArrayOf(arrayType[1]), 1)
            merger.add(field, 2)

            return XyoObjectProvider.create(merger.merge()) ?: throw XyoCorruptDataException("Cant Unpack: ${arrayType[0]}, ${arrayType[1]}")
        } else {
            throw XyoNoObjectException("Cant find size of element!, ${arrayType[0]}, ${arrayType[1]}")
        }
    }

    private fun getSize (sizeSize : Int) : Int? {
        var tempSizePosition = 0
        val size = ByteArray(sizeSize)

        if (globalCurrentPosition + (sizeSize - 1) < data.size) {
            for (i in globalCurrentPosition..globalCurrentPosition + (sizeSize - 1)) {
                size[tempSizePosition] = data[i]
                tempSizePosition++
                globalCurrentPosition++
            }
        } else {
            return null
        }


        when (sizeSize) {
            1 -> return XyoUnsignedHelper.readUnsignedByte(size)
            2 -> return XyoUnsignedHelper.readUnsignedShort(size)
            4 -> return XyoUnsignedHelper.readUnsignedInt(size)
        }

        return ByteBuffer.wrap(size).int
    }

    private fun readBytes (size : Int) : ByteArray {
        var currentPosition = 0
        val readBytes = ByteArray(size)

        for (i in currentPosition until currentPosition + size) {
            readBytes[currentPosition] = data[i + globalCurrentPosition]
            currentPosition++
        }

        return readBytes
    }

    init {
        if (!typed) {
            majorType = data[0]
            minorType = data[1]
        }
    }
}