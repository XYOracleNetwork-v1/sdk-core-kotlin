package network.xyo.sdkobjectmodelkotlin.structure

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.toHexString
import java.nio.*

/**
 * A base class for <i>XyoObjects</i>. This is used for obtaining the schema, value, and size of the item.
 */
open class XyoObjectStructure (open var item: ByteArray, open var allowedOffset: Int, open var headerSize: Int = 2) {

    /**
     * The XyoObjectSchema of the XyoObjectStructure
     */
    open val schema : XyoObjectSchema
        get() {
            return XyoObjectSchema.createFromHeader(item.copyOfRange(allowedOffset, allowedOffset + headerSize))
        }

    /**
     * The size of the object, in bytes.
     *
     * NOTE: This does not include the first two header bytes.
     */
    open val sizeBytes : Int
        get() {
            return readSizeOfObject(schema.sizeIdentifier, allowedOffset + headerSize)
        }

    /**
     * The value of the object. The value of the object is the object without the size, or the 2 byte header.
     */
    open val valueCopy : ByteArray
        get() {
            return item.copyOfRange(
                    headerSize + schema.sizeIdentifier + allowedOffset,
                    headerSize + allowedOffset + sizeBytes
            )
        }

    /**
     * All of the bytes for the object including the header and size.
     */
    open val bytesCopy : ByteArray
        get() {
            return item.copyOfRange(allowedOffset, allowedOffset + sizeBytes + headerSize)
        }


    /**
     * Reads the size of the object at a current index.
     *
     * @param sizeToReadForSize The number of bytes to read for the size.
     * @param offset The offset at which to read the size.
     * @throws XyoObjectException Ig the sizeToReadForSize is not [1, 2, 4]
     */
    protected fun readSizeOfObject (sizeToReadForSize : Int, offset: Int) : Int {
        val buffer = ByteBuffer.allocate(sizeToReadForSize)
        buffer.put(item.copyOfRange(offset, offset + sizeToReadForSize))


        when (sizeToReadForSize) {
            1 -> return buffer[0].toInt() and 0xFF
            2 -> return buffer.getShort(0).toInt() and 0xFFFF
            4 -> return buffer.getInt(0)
        }

        throw XyoObjectException("Stub for long count. Value: ${item.toHexString()}")
    }

    override fun equals(other: Any?): Boolean {
        if (other is XyoObjectStructure) {
            return other.bytesCopy.contentEquals(bytesCopy)
        }

        return false
    }

    override fun hashCode(): Int {
        return bytesCopy.contentHashCode()
    }

    companion object {
        /**
         * Creates a XyoObjectStructure with a schema and a value.
         *
         * @param schema The schema to create the object with.
         * @param value The value of the object to encode. This does NOT include size.
         */
        fun newInstance (schema : XyoObjectSchema, value : ByteArray) : XyoObjectStructure {
            return XyoObjectStructure(getObjectEncoded(schema, value), 0)
        }

        /**
         * Wraps a given XyoObjectStructure in byte form and creates a XyoObjectStructure.
         *
         * @param buff The encoded XyoObjectStructure, this value can be obtained from myBuff.bytesCopy
         * @return The represented XyoObjectStructure.
         */
        fun wrap (buff : ByteArray) : XyoObjectStructure {
            return XyoObjectStructure(buff, 0)
        }

        /**
         * Encodes a XyoObjectStructure given a value and schema.
         *
         * @param schema The schema of the XyoObjectStructure to create.
         * @param value The value of the XyoObjectStructure to create. This does NOT include size.
         */
        fun getObjectEncoded (schema: XyoObjectSchema, value: ByteArray) : ByteArray {
            val newSchema = schema.toNewSize(XyoNumberEncoder.getSmartSize(value.size))

            val buffer = ByteBuffer.allocate(value.size + newSchema.sizeIdentifier + 2)
            buffer.put(newSchema.header)
            buffer.put(XyoNumberEncoder.createSize(value.size, newSchema.sizeIdentifier))
            buffer.put(value)
            return buffer.array()
        }
    }
}