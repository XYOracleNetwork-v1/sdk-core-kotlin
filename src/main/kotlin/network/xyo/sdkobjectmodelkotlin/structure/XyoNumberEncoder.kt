package network.xyo.sdkobjectmodelkotlin.structure

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import java.nio.ByteBuffer

/**
 * A until object for encoding sizes of structure.
 */
object XyoNumberEncoder {

    /**
     * Creates the proper size for a given item.
     *
     * @param sizeOfItem The size of the item to be encoded, in bytes.
     * @param sizeOfSize The number of bytes to use for size.
     * @return The encoded size.
     * @throws XyoObjectException When the sizeOfSize is not [1, 2, 4]
     */
    fun createSize (sizeOfItem : Int, sizeOfSize : Int) : ByteArray {
        val basBuffer = ByteBuffer.allocate(sizeOfSize)

        when (sizeOfSize) {
            1 -> basBuffer.put((sizeOfItem + 1).toByte())
            2 -> basBuffer.putShort((sizeOfItem + 2).toShort())
            4 -> basBuffer.putInt((sizeOfItem + 4))
            else -> throw XyoObjectException("Not a supported count: $sizeOfItem.")
        }

        return basBuffer.array()
    }

    /**
     * Gets the best size of size to use.
     *
     * @param sizeOfItem The size, in bytes, of the item.
     * @return The number of bytes to encode the size with (XyoNumberEncoder.createSize()).
     */
    fun getSmartSize (sizeOfItem : Int) : Int {
        if (sizeOfItem + 1 <= 255) {
            return 1
        }

        if (sizeOfItem + 2 <= 65535) {
            return 2
        }

        if (sizeOfItem + 4 <= Int.MAX_VALUE) {
            return 4
        }

        return 8
    }
}