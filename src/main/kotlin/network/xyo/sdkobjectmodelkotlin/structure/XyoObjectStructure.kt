package network.xyo.sdkobjectmodelkotlin.structure

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.toHexString
import java.nio.*

@ExperimentalUnsignedTypes
enum class ObjectId(val value: UByte) {
    RsaSignature(0x0a.toUByte())
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class XyoObjectHeader {

    val bytes: UByteArray

    constructor(bytes: UByteArray) {
        this.bytes = bytes
    }

    constructor(id: UByte, flags: UByte) {
        this.bytes = ubyteArrayOf(flags, id)
    }

    constructor(id: ObjectId, flags: XyoObjectFlags) {
        this.bytes = ubyteArrayOf(flags.value, id.value)
    }
}

/**
 * A base class for <i>XyoObjects</i>. This is used for obtaining the schema, value, and size of the item.
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
open class XyoObjectStructure {

    constructor (bytes: ByteArray? = null, allowedOffset: Int? = null, headerSize: Int? = null) {
        this.bytes = bytes ?: byteArrayOf()
        this.allowedOffset = allowedOffset ?: 0
        this.headerSize = headerSize ?: 2
    }

    constructor (id: ObjectId, iterable: Boolean, typed: Boolean, data: ByteArray) {
        val flags = XyoObjectFlags(data.size, iterable, typed)
        this.bytes = ubyteArrayOf(flags.value, id.value).toByteArray() + data
    }

    constructor (header: ByteArray, data: ByteArray) {
        this.bytes = header + data
    }

    /**
     * The primary data input for the XyoObjectStructure. This buffer will not be read before the allowedOffset.
     */
    open var bytes : ByteArray

    /**
     * The starting offset of where to read. This buffer will not be read past this buffer.
     */
    var allowedOffset : Int = 0

    private var headerSize : Int = 2

    /**
     * The XyoObjectSchema of the XyoObjectStructure
     */
    val schema : XyoObjectSchema
        get() {
            return XyoObjectSchema(bytes.copyOfRange(allowedOffset, allowedOffset + headerSize))
        }

    /**
     * The size of the object, in bytes.
     *
     * NOTE: This does not include the first two header bytes.
     */
    val sizeBytes : Int
        get() {
            return readSizeOfObject(schema.sizeIdentifier.value.size, allowedOffset + headerSize)
        }

    /**
     * The value of the object. The value of the object is the object without the size, or the 2 byte header.
     */
    val valueCopy : ByteArray
        get() {
            return bytes.copyOfRange(
                    headerSize + schema.sizeIdentifier.value.size + allowedOffset,
                    headerSize + allowedOffset + sizeBytes
            )
        }

    /**
     * All of the bytes for the object including the header and size.
     */
    val bytesCopy : ByteArray
        get() {
            return bytes.copyOfRange(allowedOffset, allowedOffset + sizeBytes + headerSize)
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
        buffer.put(bytes.copyOfRange(offset, offset + sizeToReadForSize))


        when (sizeToReadForSize) {
            1 -> return buffer[0].toInt() and 0xFF
            2 -> return buffer.getShort(0).toInt() and 0xFFFF
            4 -> return buffer.getInt(0)
        }

        throw XyoObjectException("Stub for long count. Value: ${bytes.toHexString()}")
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

            val buffer = ByteBuffer.allocate(value.size + newSchema.sizeIdentifier.value.size + 2)
            buffer.put(newSchema.header)
            buffer.put(XyoNumberEncoder.createSize(value.size, newSchema.sizeIdentifier.value.size))
            buffer.put(value)
            return buffer.array()
        }

        fun concatByteArrays(a1: ByteArray, a2: ByteArray?): ByteArray {
            val result = ByteArray(a1.size + (a2?.size ?: 0))
            a1.copyInto(result, 0)
            a2?.let {
                a2.copyInto(result, a1.size)
            }
            return result
        }
    }
}