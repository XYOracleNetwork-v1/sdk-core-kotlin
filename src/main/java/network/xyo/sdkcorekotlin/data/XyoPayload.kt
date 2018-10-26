package network.xyo.sdkcorekotlin.data

import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import java.nio.ByteBuffer
import java.util.*

/**
 * A Xyo Payload used in a bound witness.
 *
 * @major 0x02
 * @minor 0x04
 *
 * @param signedPayload The payload to be signed.
 * @param unsignedPayload The payload not to be signed.
 */
class XyoPayload(val signedPayload : XyoMultiTypeArrayInt,
                 var unsignedPayload : XyoMultiTypeArrayInt) : XyoObject() {

    override val objectInBytes: ByteArray
        get() = makeEncoded()

    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = 4

    /**
     * A mapping of the signed payload.  Map of the Type to the Value.
     */
    val signedPayloadMapping :  HashMap<Int, XyoObject>
            get () = getMappingOfElements(signedPayload.array)

    /**
     * A mapping of the unsigned payload.  Map of the Type to the Value.
     */
    val unsignedPayloadMapping : HashMap<Int, XyoObject>
            get() = getMappingOfElements(unsignedPayload.array)

    /**
     * Removes a type from the unsigned payload
     *
     * @param id The id of the type to remove
     */
    fun removeTypeFromUnsigned (id : ByteArray) {
        if (unsignedPayloadMapping.containsKey(id.contentHashCode())) {
            val newArray = LinkedList<XyoObject>()

            for (item in unsignedPayload.array) {
                if (item.id.contentHashCode() != id.contentHashCode()) {
                    newArray.add(item)
                }
            }

            unsignedPayload = XyoMultiTypeArrayInt(newArray.toTypedArray())
        }
    }

    private fun getMappingOfElements (objects : Array<XyoObject>) : HashMap<Int, XyoObject> {
        val mapping = HashMap<Int, XyoObject>()
        for (element in objects) {
            mapping[element.id.contentHashCode()] = element
        }
        return mapping
    }

    private fun makeEncoded () : ByteArray {
        val merger = XyoByteArraySetter(2)
        merger.add(signedPayload.untyped, 0)
        merger.add(unsignedPayload.untyped, 1)
        return merger.merge()
    }

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x04
        override val sizeOfBytesToGetSize: Int? = 4

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).int
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val signedPayloadSize = ByteBuffer.wrap(reader.read(4, 4)).int
            val unsignedPayloadSize =  ByteBuffer.wrap(reader.read(4 + signedPayloadSize, 4)).int

            val signedPayload = reader.read(4, signedPayloadSize)
            val unsignedPayload = reader.read(4 + signedPayloadSize, unsignedPayloadSize)

            val signedPayloadCreated = XyoMultiTypeArrayInt.createFromPacked(signedPayload) as XyoMultiTypeArrayInt
            val unsignedPayloadCreated = XyoMultiTypeArrayInt.createFromPacked(unsignedPayload) as XyoMultiTypeArrayInt

            return XyoPayload(signedPayloadCreated, unsignedPayloadCreated)
        }
    }
}