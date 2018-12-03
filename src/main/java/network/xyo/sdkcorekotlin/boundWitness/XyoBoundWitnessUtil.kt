package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BW
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

object XyoBoundWitnessUtil {
    fun removeTypeFromUnsignedPayload (type : Byte, boundWitness: ByteArray) : ByteArray {
        val newPayloads = ArrayList<ByteArray>()

        for (payload in XyoIterableObject(XyoIterableObject(boundWitness)[1]).iterator) {
            val signedPayload = XyoIterableObject(payload)[0]
            val unsignedPayload = XyoIterableObject(payload)[1]
            val itemsThatAreNotTypeUnsigned = ArrayList<ByteArray>()

            for (item in XyoIterableObject(unsignedPayload).iterator) {
                if (XyoObjectSchema.createFromHeader(item.copyOfRange(0, 2)).id != type) {
                    itemsThatAreNotTypeUnsigned.add(item)
                }
            }

            val newUnsignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, itemsThatAreNotTypeUnsigned.toTypedArray())
            newPayloads.add(XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(signedPayload, newUnsignedPayload)))
        }

        return XyoObjectSetCreator.createUntypedIterableObject(BW,
                arrayOf(
                        XyoIterableObject(boundWitness)[0],
                        XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.ARRAY_TYPED, newPayloads.toTypedArray()),
                        XyoIterableObject(boundWitness)[2]
                )
            )
    }

    fun getPartyNumberFromPublicKey (boundWitness: ByteArray, publicKey: ByteArray) : Int? {
        val keys = XyoIterableObject(XyoIterableObject(boundWitness)[0])
        for (i in 0..keys.size) {
            for (key in XyoIterableObject(keys[i]).iterator) {
                val keyValue = XyoObjectCreator.getObjectValue(publicKey)
                val bwKeyValue = XyoObjectCreator.getObjectValue(key)

                if (keyValue.contentEquals(bwKeyValue)) {
                    return i
                }
            }
        }

        return null
    }
}