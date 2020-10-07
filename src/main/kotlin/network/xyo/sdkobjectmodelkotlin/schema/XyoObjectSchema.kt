package network.xyo.sdkobjectmodelkotlin.schema

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoSchemaException
import org.json.JSONObject
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * A information class used to represent all identifying factors in the XyoObjectModel. This is typically represented
 * as the first two bytes in an object (The encoding catalogue and the ID). You can create a XyoObjectSchema from a
 * structure's header (first 2 bytes), or from a JSON schema.
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class XyoObjectSchema {

    constructor(header: ByteArray) {
        if (header.size != 2) {
            throw XyoSchemaException("Expected header count to be 2, saw: ${header.size}")
        }
        this.header = header
        this.meta = null
    }

    constructor(encodingCatalog: Byte, id: Byte) {
        this.header = byteArrayOf(encodingCatalog, id)
        this.meta = null
    }

    constructor(id: Byte, isIterable: Boolean, isTyped: Boolean, sizeIdentifier: SizeIdentifier, meta: XyoObjectSchemaMeta? = null) {
        val encodingCatalog: UByte = sizeIdentifier.value.id.rotateLeft(6).or(
                if (isIterable) ITERABLE_MASK else 0.toUByte()).or(
                if (isTyped) TYPED_MASK else 0.toUByte())
        this.header = byteArrayOf(encodingCatalog.toByte(), id)
        this.meta = meta
    }

    /**
     * The ID id the the object
     */
    val id: Byte
        get() = header[1]

    /**
     * The 2 most significant bits (big endian) that represent 1, 2, 4, or 8. This value is obtained from the
     * sizeIdentifier. If the sizeIdentifier does not conform to these values, a XyoSchemaException will be
     * thrown.
     *
     * @throws XyoSchemaException when the sizeIdentifier is not 1, 2, 4, 8, or null
     */

    val sizeIdentifier: SizeIdentifier
        get() {
            val byte0 = header[0].toUByte()
            val masked = byte0.and(SIZE_IDENTIFIER_MASK)
            val shifted = masked.rotateRight(6)
            return parseSizeIdentifier(shifted)
        }

    /**
     * If the bytes in the object are iterable.
     */
    val isIterable: Boolean
        get() = header[0].toUByte().and(ITERABLE_MASK).toInt() != 0

    /**
     * If the bytes in the typed object are iterable and unique.
     */
    val isTyped: Boolean
        get() = header[0].toUByte().and(TYPED_MASK).toInt() != 0

    /**
     * A meta class to store information about the schema.
     */

    val meta: XyoObjectSchemaMeta?

    /**
     * A meta class to store information about the schema.
     */
    class XyoObjectSchemaMeta {

        constructor(name: String? = null, desc: String? = null) {
            this.name = name
            this.desc = desc
        }

        /**
         * The name of the schema.
         */
        val name : String?

        /**
         * The description of the schema.
         */
        val desc : String?
    }

    /**
     * The first byte of the object. This value contains the sizeIdentifierByte, the iterableByte, the typedByte, and
     * four reserved bits (4 least significant bits).
     */
    val encodingCatalogue : Byte
        get() {
            return sizeIdentifier.value.id.rotateLeft(6).or(
                    if (isIterable) ITERABLE_MASK else 0.toUByte()).or(
                    if (isTyped) TYPED_MASK else 0.toUByte()).toByte()
        }

    /**
     * The header of the schema or object, with the first byte being the encodingCatalogue, and the second catalogue
     * being the ID of the object.
     */
    val header : ByteArray

    fun toNewSize(newSize: Int): XyoObjectSchema {
        return XyoObjectSchema(this.encodingCatalogue, this.id)
    }

    @ExperimentalUnsignedTypes
    companion object {

        val SIZE_IDENTIFIER_MASK = (0xc0).toUByte()
        val ITERABLE_MASK = (0x20).toUByte()
        val TYPED_MASK = (0x10).toUByte()
        
        /**
         * Gets a schema meta object from a json object.
         *
         * @param jsonObject The meta JSON Object
         * @return The XyoObjectSchemaMeta of the object from JSON
         */        
        private fun getMetaFromJsonObject (jsonObject: JSONObject) : XyoObjectSchemaMeta {
            return XyoObjectSchemaMeta(jsonObject["name"] as String?, jsonObject["desc"] as String?)
        }

        /**
         * The count of the count indicator. This value can be 1, 2, 4, 8, or null. If the value is null, this value will be
         * chosen to optimize the count of the object. If the value is not 1, 2, 4, 8, or null, will throw a
         * XyoSchemaException.
         */

        class SizeIdentifierValue(
            val id: UByte,
            val size: Int
        )

        enum class SizeIdentifier(val value: SizeIdentifierValue) {
            One(SizeIdentifierValue(0x00.toUByte(), 1)),
            Two(SizeIdentifierValue(0x01.toUByte(), 2)),
            Four(SizeIdentifierValue(0x02.toUByte(), 4)),
            Eight(SizeIdentifierValue(0x03.toUByte(), 8))
        }

        fun parseSizeIdentifier(value: UByte): SizeIdentifier {
            return when (value) {
                SizeIdentifier.One.value.id -> SizeIdentifier.One
                SizeIdentifier.Two.value.id -> SizeIdentifier.Two
                SizeIdentifier.Four.value.id -> SizeIdentifier.Four
                SizeIdentifier.Eight.value.id -> SizeIdentifier.Eight
                else -> throw XyoSchemaException("Invalid Size: ${value.toString(2)}")
            }
        }
    }
}