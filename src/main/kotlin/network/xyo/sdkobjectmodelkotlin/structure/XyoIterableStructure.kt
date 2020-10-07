package network.xyo.sdkobjectmodelkotlin.structure

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectIteratorException
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.toHexString
import org.json.JSONArray
import java.nio.ByteBuffer

/**
 * An Iterator for iterating over sets.
 */
@ExperimentalStdlibApi
open class XyoIterableStructure : XyoObjectStructure {

    constructor (item: ByteArray, allowedOffset: Int) : super(item, allowedOffset)
    constructor (item: ByteArray, allowedOffset: Int, headerSize: Int) : super(item, allowedOffset, headerSize)

    /**
     * The global schema of of the iterator. This value is null when iterating over an untyped set, and is not null
     * when iterating over a typed set.
     */
    private var globalSchema : XyoObjectSchema? = null

    /**
     * Gets an instance of a new iterator to iterate over the set.
     *
     * @throws XyoObjectIteratorException If the bytes are malformed.
     */
    open val iterator : Iterator<XyoObjectStructure>
        get() {
            return XyoObjectIterator(readOwnHeader())
        }

    /**
     * Gets the number of elements in the array.
     *
     * @throws XyoObjectIteratorException If the bytes are malformed.
     */
    open val count : Int
        get() {

            val sizeIt = iterator
            var i = 0
            while (sizeIt.hasNext()) {
                i++
                sizeIt.next()
            }
            return i
        }


    /**
     * Reads the current item at an offset.
     *
     * @param startingOffset The offset at which to read an item from.
     * @return The XyoObjectStructure at that offset.
     * @throws XyoObjectIteratorException If the bytes are malformed.
     */
    private fun readItemAtOffset (startingOffset : Int) : XyoObjectStructure {
        if (globalSchema == null) {
            return readItemUntyped(startingOffset)
        }
        return readItemTyped(startingOffset)
    }

    /**
     * Reads an item from an untyped array the startingOffset.
     *
     * @param startingOffset Where to read the item from.
     * @throws XyoObjectIteratorException If the bytes are malformed.
     */
    private fun readItemUntyped (startingOffset: Int) : XyoObjectStructure {
        val schemaOfItem = getNextHeader(startingOffset)
        checkIndex(startingOffset + 2 + schemaOfItem.sizeIdentifier.value.size)
        val sizeOfObject = readSizeOfObject(schemaOfItem.sizeIdentifier.value.size, startingOffset + 2)

        if (sizeOfObject == 0) {
            throw XyoObjectIteratorException("Size can not be 0. Value: ${bytes.toHexString()}")
        }

        checkIndex(startingOffset + sizeOfObject + 2)

        if (schemaOfItem.isIterable) {
            return  XyoIterableStructure(this@XyoIterableStructure.bytes, startingOffset)
        }

        return  XyoObjectStructure(this@XyoIterableStructure.bytes, startingOffset)
    }


    /**
     * Reads an item from an typed array the startingOffset.
     *
     * @param startingOffset Where to read the item from.
     * @throws XyoObjectIteratorException If the bytes are malformed.
     */
    private fun readItemTyped (startingOffset: Int) : XyoObjectStructure {
        val schemaOfItem =  globalSchema ?: throw XyoObjectIteratorException("Global schema is null!")
        val sizeOfObject = readSizeOfObject(schemaOfItem.sizeIdentifier.value.size, startingOffset)

        if (sizeOfObject == 0) {
            throw XyoObjectIteratorException("Size can not be 0. Value: ${bytes.toHexString()}")
        }

        val buffer = ByteBuffer.allocate(sizeOfObject + 2)
        checkIndex(startingOffset + sizeOfObject)

        buffer.put(schemaOfItem.header)
        buffer.put(bytes.copyOfRange(startingOffset, startingOffset + sizeOfObject))

        if (schemaOfItem.isIterable) {
            return XyoIterableStructure(buffer.array(), 0)
        }

        return  XyoObjectStructure(buffer.array(), 0)
    }

    /**
     * Gets an element at a certain index.
     *
     * @param index The index to get the item from.
     * @return The item at that index.
     * @throws XyoObjectIteratorException if the bytes are malformed or if the index is out of range.
     */
    open operator fun get(index: Int): XyoObjectStructure {
        val it = iterator
        var i = 0

        while (it.hasNext()) {
            val item = it.next()

            if (i == index) {
                return item
            }

            i++
        }

        throw XyoObjectIteratorException("Index out of range! Size $i, Index: $index. Value: ${bytes.toHexString()}")
    }


    /**
     * Gets all of the elements in an array that are of a certain type.
     *
     * @param type The type of the elements to look for.
     * @return An array of possible items that have that ID.
     * @throws XyoObjectIteratorException if the bytes are malformed.
     */
    open operator fun get(type: Byte): Array<XyoObjectStructure> {
        val it = iterator
        val itemsThatFollowTheType = ArrayList<XyoObjectStructure>()

        while (it.hasNext()) {
            val next = it.next()
            if (next.schema.id == type) {
                itemsThatFollowTheType.add(next)
            }
        }

        return itemsThatFollowTheType.toTypedArray()
    }

    /**
     * Gets the next object schema at the current offset.
     *
     * @param offset The offset at which to read the header from.
     */
    private fun getNextHeader (offset : Int) : XyoObjectSchema {
        checkIndex(offset + 2)
        return  XyoObjectSchema(bytes.copyOfRange(offset, offset + 2))
    }

    /**
     * Gets the index of the current buffer to see if there is space.
     *
     * @throws XyoObjectIteratorException If there is not enough space to read from.
     */
    private fun checkIndex (index: Int) {
        if (index > bytes.size) {
            throw XyoObjectIteratorException("Out of count. Value: ${bytes.toHexString()}, Offset: $index")
        }
    }

    /**
     * An iterator class to help iterate over the set.
     *
     * @param currentOffset Where to start the iterator. (The offset of the first element)
     */
    inner class XyoObjectIterator (private var currentOffset: Int) : Iterator<XyoObjectStructure> {

        /**
         * Checks if there is another item in the set.
         *
         * @return True if there is another element in the set.
         */
        override fun hasNext(): Boolean {
            return allowedOffset + sizeBytes + 2  > currentOffset
        }

        /**
         * Gets the next item in the set.
         *
         * @throws XyoObjectIteratorException If the bytes are malformed or if the index is out of range.
         */
        override fun next(): XyoObjectStructure {
            val nextItem = readItemAtOffset(currentOffset)

            if (globalSchema == null) {
                currentOffset += nextItem.sizeBytes + 2
            } else {
                currentOffset += nextItem.sizeBytes
            }

            return nextItem
        }
    }

    /**
     * Reads the header of the iterable object. Reads the first two bytes and size.
     *
     * @return Where the first element's offset is.
     * @throws XyoObjectIteratorException If the bytes are malformed.
     */
    private fun readOwnHeader () : Int {
        val setHeader = getNextHeader(allowedOffset)
        val totalSize = readSizeOfObject(setHeader.sizeIdentifier.value.size, allowedOffset + 2)

        if (!setHeader.isIterable) {
            throw XyoObjectIteratorException("Can not iterate on object that is not iterable. Header " +
                    "${setHeader.header[allowedOffset]}, ${setHeader.header[allowedOffset + 1]}. Value: ${bytes.toHexString()}")
        }

        if (setHeader.isTyped && totalSize != setHeader.sizeIdentifier.value.size) {
            globalSchema = getNextHeader(setHeader.sizeIdentifier.value.size + 2 + allowedOffset)
            return 4 + setHeader.sizeIdentifier.value.size + allowedOffset
        }

        return 2 + setHeader.sizeIdentifier.value.size + allowedOffset
    }

    /**
     * Converts the current iterator to a JSON string.
     *
     * @return A JSON encoded string.
     */
    override fun toString(): String {
        val rootJsonObject = JSONArray()

        if (schema.isIterable) {
            for (subItem in iterator) {
                rootJsonObject.put(JSONArray((XyoIterableStructure(subItem.bytesCopy, 0)).toString()))
            }
        } else {
            rootJsonObject.put(bytes.toHexString())
        }

        return rootJsonObject.toString()
    }

    companion object {

        /**
         * Converts an array of structure to a single type.
         *
         * @param array The array of buffs to convert.
         * @param type The type to convert it to.
         * @throws XyoObjectException if the type can not be converted (wrong IDs).
         */
        fun convertObjectsToType (array : Array<XyoObjectStructure>, type: XyoObjectSchema) : Array<XyoObjectStructure> {
            val newValues = ArrayList<XyoObjectStructure>()

            for (value in array) {
                if (value.schema.id != type.id) {
                    throw XyoObjectException("Can not convert types! ${value.schema.id}, ${type.id}")
                }

                newValues.add(XyoObjectStructure.newInstance(type, value.valueCopy))
            }

            return newValues.toTypedArray()
        }

        /**
         * Creates an untyped array. (An array that can contain different types of structure)
         *
         * @param schema The schema of the array to encode.
         * @param values The values to encode into the typed set.
         * @throws XyoObjectException If the bytes are malformed.
         * @return The iterable object.
         */
        fun createUntypedIterableObject (schema: XyoObjectSchema, values: Array<XyoObjectStructure>) : XyoIterableStructure {
            if (schema.isTyped) {
                throw XyoObjectException("Can not create untyped object from typed schema!")
            }

            var totalSize = 0

            for (item in values) {
                totalSize += item.sizeBytes + 2
            }

            val buffer = ByteBuffer.allocate(totalSize)

            for (item in values) {
                buffer.put(item.bytesCopy)
            }

            return XyoIterableStructure(XyoObjectStructure.newInstance(schema, buffer.array()).bytesCopy, 0)
        }

        /**
         * Creates a typed array. (An array that can only contain a single type of object).
         *
         * @param schema The schema of the array to encode.
         * @param values The values to encode into the typed set.
         * @throws XyoObjectException If the bytes are malformed.
         * @return The iterable object.
         */
        fun createTypedIterableObject (schema: XyoObjectSchema, values: Array<XyoObjectStructure>) : XyoIterableStructure {
            if (!schema.isTyped) {
                throw XyoObjectException("Cannot create typed object from untyped schema!")
            }

            var totalSize = 2

            if (values.isEmpty()) {
                totalSize = 0
            }

            for (item in values) {
                totalSize += item.sizeBytes
            }

            val buffer = ByteBuffer.allocate(totalSize)
            if (values.isNotEmpty()) {
                buffer.put(values[0].bytesCopy.copyOfRange(0, 2))

                for (item in values) {
                    buffer.put(item.bytesCopy.copyOfRange(2, item.sizeBytes + 2))
                }
            }

            return XyoIterableStructure(XyoObjectStructure.getObjectEncoded(schema, buffer.array()), 0)
        }
    }
}