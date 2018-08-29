package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import java.nio.ByteBuffer

class XyoArrayUnpacker (data : ByteArray, typed: Boolean, sizeOfSize: Int) {
    private val mData = data
    private val mTyped = typed
    private val mSizeOfSize = sizeOfSize
    private var mCurrentPosition = 0
    var majorType: Byte? = null
    var minorType: Byte? = null
    val array : ArrayList<XyoObject> = unpack()

    private fun getMajorMinor () : ByteArray {
        val major = mData[mCurrentPosition]
        val minor = mData[mCurrentPosition + 1]
        mCurrentPosition += 2
        return byteArrayOf(major, minor)
    }

    private fun readCurrentSize (major: Byte, minor: Byte) : Int? {
        val typeObject = XyoObjectCreator.getCreator(major, minor).value
        if (typeObject != null) {
            val sizeOfBytesToRead = typeObject.sizeOfBytesToGetSize
            return typeObject.readSize(readBytes(sizeOfBytesToRead.value!!)).value
        }
        throw Exception("Can not find $major, $minor")
    }

    private fun unpack () : ArrayList<XyoObject> {
        val expectedSize = getSize(mSizeOfSize) - mSizeOfSize
        val items = ArrayList<XyoObject>()
        var arrayType : ByteArray = byteArrayOf()
        if (mTyped) {
            arrayType = getMajorMinor()
            majorType = arrayType[0]
            minorType = arrayType[1]
        }


        while (mCurrentPosition < mData.size) {
            if (!mTyped) {
                arrayType = getMajorMinor()
            }

            val sizeOfElement = readCurrentSize(arrayType[0], arrayType[1])
            if (sizeOfElement != null) {
                val field = ByteArray(sizeOfElement)
                var position = 0

                for (i in mCurrentPosition..mCurrentPosition + (sizeOfElement - 1)) {
                    val byte = mData[i]
                    field[position] = byte
                    position++
                }

                mCurrentPosition += sizeOfElement

                val merger = XyoByteArraySetter(3)
                merger.add(byteArrayOf(arrayType[0]), 0)
                merger.add(byteArrayOf(arrayType[1]), 1)
                merger.add(field, 2)

                items.add(XyoObjectCreator.create(merger.merge()).value!!)
            }
        }

        return items
    }

    private fun getSize (sizeSize : Int) : Int {
        var tempSizePosition = 0
        val size = ByteArray(sizeSize)

        for (i in mCurrentPosition..mCurrentPosition + (sizeSize - 1)) {
            size[tempSizePosition] = mData[i]
            tempSizePosition++
            mCurrentPosition++
        }

        when (sizeSize) {
            1 -> return size[0].toInt()
            2 -> return ByteBuffer.wrap(size).short.toInt()
            4 -> return ByteBuffer.wrap(size).int
        }

        return ByteBuffer.wrap(size).int
    }

    private fun readBytes (size : Int) : ByteArray {
        var currentPosition = 0
        val readBytes = ByteArray(size)

        for (i in currentPosition until currentPosition + size) {
            readBytes[currentPosition] = mData[i + mCurrentPosition]
            currentPosition++
        }

        return readBytes
    }
}