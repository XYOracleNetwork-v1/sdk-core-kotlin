package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure

/**
 * A class to verify if a single bound witness is valid (does not validate indexes and hashes).
 *
 * @property allowUnknown If set to true, if an unknown signing type is valid. If false, an unknown signing type is not
 * valid.
 */
class XyoBoundWitnessVerify (private val allowUnknown : Boolean) {

    /**
     * Will verify a single bound witness.
     *
     * @param boundWitness The bound witness to verify
     * @return If the bound witness was successful and null if does not have the capability to
     * verify (cant find verify method)
     */
    suspend fun verify (boundWitness: XyoBoundWitness) : Boolean? {
        if (!boundWitness.completed) {
            return false
        }

        return checkAllSignatures(boundWitness.signingData, boundWitness)
    }

    /**
     * Loops through the signatures and checks each signature.
     *
     * @param signingData The data signed from the bound witness.
     * @param boundWitness The bound witness containing the signatures to check.
     * @return Will return the validity if the bound witness in terms of signatures.
     */
    private suspend fun checkAllSignatures(signingData: ByteArray, boundWitness: XyoBoundWitness) : Boolean {
        for (partyNum in 0 until (boundWitness.numberOfParties ?: 0) ) {
            val keySet = boundWitness.getFetterOfParty(partyNum)?.get(XyoSchemas.KEY_SET.id)?.getOrNull(0) as? XyoIterableStructure
            val sigSet = boundWitness.getWitnessOfParty(partyNum)?.get(XyoSchemas.SIGNATURE_SET.id)?.getOrNull(0) as? XyoIterableStructure

            if (!(keySet is XyoIterableStructure
                            && sigSet is XyoIterableStructure
                            && checkSinglePartySignatures(keySet, sigSet, signingData))) {

                return false
            }
        }

        return true
    }

    /**
     * Checks a single parties signatures.
     *
     * @param keys The key set of the party
     * @param signatures The signature set of the party
     * @param signingData The data signed in the bound witness.
     * @return The validity of the signatures
     */
    private suspend fun checkSinglePartySignatures (keys: XyoIterableStructure, signatures : XyoIterableStructure, signingData : ByteArray) : Boolean {
        for (keyNum in 0 until keys.count) {
            val key = keys[keyNum]
            val signature = signatures[keyNum]

            val verify = XyoSigner.verify(key, signature, signingData)
            
            if ((verify == null && !allowUnknown) || verify == false) {
                return false
            }
        }

        return true
    }
}