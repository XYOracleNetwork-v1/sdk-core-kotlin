package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

abstract class XyoPayload : XyoInterpreter {
    override val schema: XyoObjectSchema
        get() = XyoSchemas.PAYLOAD

    val signedPayload : XyoIterableObject
        get() = XyoIterableObject(XyoIterableObject(self)[0])

    val unsignedPayload : XyoIterableObject
        get() = XyoIterableObject(XyoIterableObject(self)[1])

    companion object : XyoFromSelf {
        override fun getInstance(byteArray: ByteArray): XyoPayload {
            return object : XyoPayload() {
                override val self: ByteArray
                    get() = byteArray
            }
        }
    }
}