package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
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
                       private val sizeOfSize: Int) {

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
     * The unpacked array wrapped in a XyoResult.
     */
    val array : XyoResult<ArrayList<XyoObject>> = unpack()

    private fun getMajorMinor () : ByteArray {
        val major = data[globalCurrentPosition]
        val minor = data[globalCurrentPosition + 1]
        globalCurrentPosition += 2
        return byteArrayOf(major, minor)
    }

    private fun readCurrentSizeFromType (major: Byte, minor: Byte) : Int? {
        val typeObject = XyoObjectProvider.getCreator(major, minor).value
        if (typeObject != null) {
            val sizeOfBytesToRead = typeObject.sizeOfBytesToGetSize
            val sizeOfBytesToReadValue = sizeOfBytesToRead.value ?: return null
            if (sizeOfBytesToReadValue + globalCurrentPosition > data.size) return null
            return typeObject.readSize(readBytes(sizeOfBytesToReadValue)).value
        }
        return null
    }

    private fun unpack () : XyoResult<ArrayList<XyoObject>> {
        val expectedSize = getSize(sizeOfSize)
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
                    return XyoResult(XyoError(this.toString(), "Cant unpack array! Not enough objectInBytes!"))
                }
            }

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
                    return XyoResult(XyoError(this.toString(), "Cant unpack array! Not enough objectInBytes!"))
                }

                globalCurrentPosition += sizeOfElement

                val merger = XyoByteArraySetter(3)
                merger.add(byteArrayOf(arrayType[0]), 0)
                merger.add(byteArrayOf(arrayType[1]), 1)
                merger.add(field, 2)

                val createdObject = XyoObjectProvider.create(merger.merge())
                val createdObjectValue = createdObject.value
                if (createdObjectValue != null) {
                    items.add(createdObjectValue)
                }
            } else {
                println("Cant find size of element!, ${arrayType[0]}, ${arrayType[1]}")
                return XyoResult(XyoError(this.toString(), "Cant find size of element!, ${arrayType[0]}, ${arrayType[1]}"))
            }
        }

        return XyoResult(items)
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
}