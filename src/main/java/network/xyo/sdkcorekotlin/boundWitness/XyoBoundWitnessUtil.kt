package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.XyoLog
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.toHexString

object XyoBoundWitnessUtil {
    fun removeTypeFromUnsignedPayload (type : Byte, boundWitness: XyoIterableObject) : XyoIterableObject {
        val newBoundWitnessLedger = ArrayList<XyoBuff>()

        val fetters = boundWitness[XyoSchemas.FETTER.id]
        val witnesses = boundWitness[XyoSchemas.WITNESS.id]

        newBoundWitnessLedger.addAll(fetters)

        for (witness in witnesses) {
            if (witness is XyoIterableObject) {
                val items = ArrayList<XyoBuff>()

                for (item in witness.iterator) {
                    if (item.schema.id != type) {
                        items.add(item)
                    } else {
                        XyoLog.logDebug("Found Item", "BWU")
                    }
                }

                newBoundWitnessLedger.add(XyoIterableObject.createUntypedIterableObject(XyoSchemas.WITNESS, items.toTypedArray()))
            }
        }

        return XyoIterableObject.createUntypedIterableObject(XyoSchemas.BW, newBoundWitnessLedger.toTypedArray())
    }

    fun getPartyNumberFromPublicKey (boundWitness: XyoBoundWitness, publicKey: XyoBuff) : Int? {
        for (i in 0 until (boundWitness.numberOfParties ?: 0)) {

            val fetter = boundWitness.getFetterOfParty(i) ?: return null
            if (checkPartyForPublicKey(fetter, publicKey) == true) {
                return i
            }

        }

        return null
    }

    fun getBridgedBlocks (boundWitness: XyoBoundWitness) : Iterator<XyoBuff>? {
        for (witness in boundWitness[XyoSchemas.WITNESS.id]) {
            if (witness is XyoIterableObject) {
                for (item in witness[XyoSchemas.BRIDGE_BLOCK_SET.id].iterator()) {
                    if (item is XyoIterableObject) {
                        return item.iterator
                    }
                }
            }
        }

        return null
    }

    private fun checkPartyForPublicKey (fetter : XyoIterableObject, publicKey : XyoBuff) : Boolean? {

        for (keySet in fetter[XyoSchemas.KEY_SET.id]) {
            if (!(keySet is XyoIterableObject)) return null

            for (key in keySet.iterator) {
                if (key == publicKey) {
                    return true
                }
            }
        }

        return false
    }
}