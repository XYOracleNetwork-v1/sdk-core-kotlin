package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject

/**
 * A helper object to preform operations on bound witnesses.
 */
object XyoBoundWitnessUtil {

    /**
     * Removes a type from the bound witness witnesses and returns a new bound witness object wrought the type.
     *
     * @param type The type to remove from the bound witness fetters.
     * @param boundWitness The bound witness to remove the type from.
     * @return A bound witness without the type specified.
     */
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

    /**
     *  Gets the index of the part of the bound witness (What index is their fetter).
     *
     * @param boundWitness The bound witness to get the party index from
     * @param publicKey The public key to look for
     * @return Returns the index of the party, if no corresponding public key is found: will return null.
     */
    fun getPartyNumberFromPublicKey (boundWitness: XyoBoundWitness, publicKey: XyoBuff) : Int? {
        for (i in 0 until (boundWitness.numberOfParties ?: 0)) {

            val fetter = boundWitness.getFetterOfParty(i) ?: return null
            if (checkPartyForPublicKey(fetter, publicKey)) {
                return i
            }

        }

        return null
    }

    /**
     * Gets the bridged blocks from a bound witness.
     *
     * @param boundWitness The bound witness to get the bridge blocks from
     * @return Bridged blocks from the bound witness. Will return null if none are found.
     */
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

    /**
     * Checks to see if there is a public key in a fetter.
     *
     * @param fetter The fetter to check for the public key.
     * @param publicKey The public key to look for in the fetter.
     * @return Will return true if the public key is in the fetter, otherwise not.
     */
    private fun checkPartyForPublicKey (fetter : XyoIterableObject, publicKey : XyoBuff) : Boolean {
        for (keySet in fetter[XyoSchemas.KEY_SET.id]) {
            if ((keySet !is XyoIterableObject)) return false

            for (key in keySet.iterator) {
                if (key == publicKey) {
                    return true
                }
            }
        }

        return false
    }
}