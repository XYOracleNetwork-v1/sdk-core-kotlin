package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BW
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectIterator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

object XyoBoundWitnessUtil {
    fun removeTypeFromUnsignedPayload (type : Byte, boundWitness: ByteArray) : ByteArray {
        val newPayloads = ArrayList<ByteArray>()

        for (payload in XyoObjectIterator(XyoObjectIterator(boundWitness)[1])) {
            val signedPayload = XyoObjectIterator(payload)[0]
            val unsignedPayload = XyoObjectIterator(payload)[1]
            val itemsThatAreNotTypeUnsigned = ArrayList<ByteArray>()

            for (item in XyoObjectIterator(unsignedPayload)) {
                if (XyoObjectSchema.createFromHeader(item.copyOfRange(0, 2)).id != type) {
                    itemsThatAreNotTypeUnsigned.add(item)
                }
            }

            val newUnsignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newPayloads.toTypedArray())
            newPayloads.add(XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(signedPayload, newUnsignedPayload)))
        }

        return XyoObjectSetCreator.createUntypedIterableObject(BW,
                arrayOf(
                        XyoObjectIterator(boundWitness)[0],
                        XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED, newPayloads.toTypedArray()),
                        XyoObjectIterator(boundWitness)[2]
                )
            )
    }
}