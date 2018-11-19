package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * The next public key heuristic.
 *
 * @major 0x02
 * @minor 0x07
 */
abstract class XyoNextPublicKey : XyoInterpreter {

    @ExperimentalUnsignedTypes
    val nextPublicKey : ByteArray
        get() {
            return XyoObjectCreator.getObjectValue(self)
        }

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.NEXT_PUBLIC_KEY

    companion object : XyoFromSelf {

        @ExperimentalUnsignedTypes
        override fun getInstance(byteArray: ByteArray): XyoNextPublicKey {
            return object : XyoNextPublicKey() {

                override val self: ByteArray
                    get() = byteArray
            }
        }

        @ExperimentalUnsignedTypes
        fun createFromHash (nextPublicKey: ByteArray): XyoNextPublicKey {
            return object : XyoNextPublicKey() {
                override val self: ByteArray
                    get() = XyoObjectCreator.createObject(schema, nextPublicKey)
            }
        }
    }
}