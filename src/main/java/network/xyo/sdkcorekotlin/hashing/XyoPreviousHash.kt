package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * A Xyo Previous Hash heuristic.
 *
 * @major 0x02
 * @minor 0x05
 */
abstract class XyoPreviousHash : XyoInterpreter {

    @ExperimentalUnsignedTypes
    val previousHash : ByteArray
        get() {
            return XyoObjectCreator.getObjectValue(self)
        }

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.PREVIOUS_HASH

    companion object : XyoFromSelf {

        @ExperimentalUnsignedTypes
        override fun getInstance(byteArray: ByteArray): XyoPreviousHash {
            return object : XyoPreviousHash() {

                override val self: ByteArray
                    get() = byteArray
            }
        }

        @ExperimentalUnsignedTypes
        fun createFromHash (hash: ByteArray): XyoPreviousHash {
            return object : XyoPreviousHash() {
                override val self: ByteArray
                    get() = XyoObjectCreator.createObject(schema, hash)
            }
        }
    }
}