package network.xyo.sdkobjectmodelkotlin.schema

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoSchemaException
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * A information class used to represent all identifying factors in the XyoObjectModel. This is typically represented
 * as the first two bytes in an object (The encoding catalogue and the ID). You can create a XyoObjectSchema from a
 * structure's header (first 2 bytes), or from a JSON schema.
 */
abstract class XyoObjectSchema {
    /**
     * The ID id the the object
     */
    abstract val id : Byte

    /**
     * The count of the count indicator. This value can be 1, 2, 4, 8, or null. If the value is null, this value will be
     * chosen to optimize the count of the object. If the value is not 1, 2, 4, 8, or null, will throw a
     * XyoSchemaException.
     */
    abstract val sizeIdentifier : Int

    /**
     * If the bytes in the object are iterable.
     */
    abstract val isIterable : Boolean

    /**
     * If the bytes in the typed object are iterable and unique.
     */
    abstract val isTyped : Boolean

    /**
     * A meta class to store information about the schema.
     */
    abstract val meta : XyoObjectSchemaMeta?

    /**
     * A meta class to store information about the schema.
     */
    abstract class XyoObjectSchemaMeta {
        /**
         * The name of the schema.
         */
        abstract val name : String?

        /**
         * The description of the schema.
         */
        abstract val desc : String?
    }

    /**
     * The 2 most significant bits (big endian) that represent 1, 2, 4, or 8. This value is obtained from the
     * sizeIdentifier. If the sizeIdentifier does not conform to these values, a XyoSchemaException will be
     * thrown.
     *
     * @throws XyoSchemaException when the sizeIdentifier is not 1, 2, 4, 8, or null
     */
    private val sizeIdentifierByte : Byte
        get() {
            when (sizeIdentifier) {
                1 -> return (0x00)
                2 -> return (0x40)
                4 -> return (0x80.toByte())
                8 -> return (0xC0.toByte())
            }
            throw XyoSchemaException("Invalid Size $sizeIdentifier")
        }

    /**
     * The 3rd most significant bit that represents if the object is iterable. This value is obtained from
     * isIterable.
     */
    private val iterableByte : Byte
        get() {
            if (isIterable) {
                return 0x20
            }

            return 0x00
        }

    /**
     * The 4th most significant bit that represents if the following object is typed.
     */
    private val typedByte : Byte
        get() {
            if (isTyped) {
                return (0x10.toByte())
            }

            return 0x00.toByte()
        }

    /**
     * Converts the current schema to a new schema with a different size
     *
     * @param newSize The new size of the schema to create
     * @return XyoObjectSchema With the new size
     */
    fun toNewSize (newSize : Int) : XyoObjectSchema {
        return object : XyoObjectSchema() {
            override val id: Byte = this@XyoObjectSchema.id
            override val isIterable: Boolean = this@XyoObjectSchema.isIterable
            override val isTyped: Boolean = this@XyoObjectSchema.isTyped
            override val meta: XyoObjectSchemaMeta? = this@XyoObjectSchema.meta
            override val sizeIdentifier: Int = newSize
        }
    }

    /**
     * The first byte of the object. This value contains the sizeIdentifierByte, the iterableByte, the typedByte, and
     * four reserved bits (4 least significant bits).
     */
    val encodingCatalogue : Byte
        get() {
            return sizeIdentifierByte or iterableByte or typedByte
        }

    /**
     * The header of the schema or object, with the first byte being the encodingCatalogue, and the second catalogue
     * being the ID of the object.
     */
    val header : ByteArray
        get() {
            return byteArrayOf(encodingCatalogue, id)
        }

    companion object {

        /**
         * Creates a XyoObjectSchema from a given header. Note that all combinations of 2 bytes are valid, therefore
         * make sure that the header being passed to the function is the correct header. The header must be 2 bytes,
         * or a XyoSchemaException will be thrown.
         *
         * @param byteArray The header of the item to create a schema from.
         * @return A XyoObjectSchema of the schema of the Header.
         * @throws XyoSchemaException When the header size is not 2.
         */
        fun createFromHeader (header: ByteArray) : XyoObjectSchema {
            if (header.size != 2) {
                throw XyoSchemaException("Expected header count to be 2, saw: ${header.size}")
            }

            return object : XyoObjectSchema() {
                override val id: Byte
                    get() = header[1]

                override val isIterable: Boolean
                    get() = (header[0] and 0x20).toInt() != 0

                override val isTyped: Boolean
                    get() = (header[0] and 0x10).toInt() != 0

                override val meta: XyoObjectSchemaMeta? = null

                override val sizeIdentifier: Int
                    get() = readSizeIdentifierFromEncodingCatalogue(header[0])
            }
        }

        /**
         * Checks the count identifier from the encodingCatalogue. The 2 most significant bits.
         *
         * @param encodingCatalogue The encodingCatalogue of the header. (The first byte)
         * @return [1, 2, 4, or 8] depending on the encoding catalogue.
         */
        private fun readSizeIdentifierFromEncodingCatalogue (encodingCatalogue: Byte) : Int {

            // masking the first two bits to get the result
            // 0xC0 == 11000000

            if (encodingCatalogue and 0xC0.toByte() == 0x00.toByte()) {
                return 1
            }

            if (encodingCatalogue and 0xC0.toByte() == 0x40.toByte()) {
                return 2
            }

            if (encodingCatalogue and 0xC0.toByte() == 0x80.toByte()) {
                return 4
            }

            if (encodingCatalogue and 0xC0.toByte() == 0xC0.toByte()) {
                return 8
            }

            throw XyoSchemaException("Invalid Size: ${encodingCatalogue.toString(2)}")
        }
    }
}