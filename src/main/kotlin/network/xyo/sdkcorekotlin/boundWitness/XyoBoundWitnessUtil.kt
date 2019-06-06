package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * A helper object to preform PURE operations on bound witnesses, meaning irrelevant to origin related topics. For
 * Origin related items, please see XyoOriginBoundWitnessUtil.
 */
object XyoBoundWitnessUtil {

    /**
     * Removes a type from the bound witness witnesses and returns a new bound witness object wrought the type.
     *
     * @param type The type to remove from the bound witness fetters.
     * @param boundWitness The bound witness to remove the type from.
     * @return A bound witness without the type specified.
     */
    fun removeTypeFromUnsignedPayload (type : Byte, boundWitness: XyoIterableStructure) : XyoIterableStructure {
        val newBoundWitnessLedger = ArrayList<XyoObjectStructure>()

        val fetters = boundWitness[XyoSchemas.FETTER.id]
        val witnesses = boundWitness[XyoSchemas.WITNESS.id]

        newBoundWitnessLedger.addAll(fetters)

        for (witness in witnesses) {
            if (witness is XyoIterableStructure) {
                val items = ArrayList<XyoObjectStructure>()

                for (item in witness.iterator) {
                    if (item.schema.id != type) {
                        items.add(item)
                    } else {
                        XyoLog.logDebug("Found Item", "BWU")
                    }
                }

                newBoundWitnessLedger.add(XyoIterableStructure.createUntypedIterableObject(XyoSchemas.WITNESS, items.toTypedArray()))
            }
        }

        return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.BW, newBoundWitnessLedger.toTypedArray())
    }

    /**
     *  Gets the index of the part of the bound witness (What index is their fetter).
     *
     * @param boundWitness The bound witness to get the party index from
     * @param publicKey The public key to look for
     * @return Returns the index of the party, if no corresponding public key is found: will return null.
     */
    fun getPartyNumberFromPublicKey (boundWitness: XyoBoundWitness, publicKey: XyoObjectStructure) : Int? {
        for (i in 0 until (boundWitness.numberOfParties ?: 0)) {

            val fetter = boundWitness.getFetterOfParty(i) ?: return null
            if (XyoBoundWitnessUtil.checkPartyForPublicKey(fetter, publicKey)) {
                return i
            }

        }

        return null
    }

    /**
     * Checks to see if there is a public key in a fetter.
     *
     * @param fetter The fetter to check for the public key.
     * @param publicKey The public key to look for in the fetter.
     * @return Will return true if the public key is in the fetter, otherwise not.
     */
    private fun checkPartyForPublicKey (fetter : XyoIterableStructure, publicKey : XyoObjectStructure) : Boolean {
        for (keySet in fetter[XyoSchemas.KEY_SET.id]) {
            if ((keySet !is XyoIterableStructure)) return false

            for (key in keySet.iterator) {
                if (key == publicKey) {
                    return true
                }
            }
        }

        return false
    }
}