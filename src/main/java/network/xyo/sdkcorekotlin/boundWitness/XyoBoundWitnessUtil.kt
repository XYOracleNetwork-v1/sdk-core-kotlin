package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BW
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

object XyoBoundWitnessUtil {
    fun removeTypeFromUnsignedPayload (type : Byte, boundWitness: XyoIterableObject) : XyoIterableObject {
        val newBoundWitnessLedger = ArrayList<XyoBuff>()

        val fetters = boundWitness[XyoSchemas.FETTER.id]
        val witnesses = boundWitness[XyoSchemas.WITNESSS.id]

        newBoundWitnessLedger.addAll(fetters)

        for (witness in witnesses) {
            if (witness is XyoIterableObject) {
                val items = ArrayList<XyoBuff>()

                for (item in witness.iterator) {
                    if (item.schema.id != type) {
                        items.add(item)
                    }
                }

                newBoundWitnessLedger.add(XyoIterableObject.createUntypedIterableObject(XyoSchemas.WITNESSS, items.toTypedArray()))
            }
        }

        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.BW, newBoundWitnessLedger.toTypedArray())
    }

    fun getPartyNumberFromPublicKey (boundWitness: ByteArray, publicKey: ByteArray) : Int? {
//        val keys = XyoIterableObject(XyoIterableObject(boundWitness)[0])
//        for (i in 0..keys.size) {
//            for (key in XyoIterableObject(keys[i]).iterator) {
//                val keyValue = XyoObjectCreator.getObjectValue(publicKey)
//                val bwKeyValue = XyoObjectCreator.getObjectValue(key)
//
//                if (keyValue.contentEquals(bwKeyValue)) {
//                    return i
//                }
//            }
//        }

        return null
    }
}