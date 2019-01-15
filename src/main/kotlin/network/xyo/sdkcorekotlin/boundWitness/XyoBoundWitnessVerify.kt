package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject

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
    fun verify (boundWitness: XyoBoundWitness) : Deferred<Boolean?> = GlobalScope.async {
        if (!boundWitness.completed) {
            return@async false
        }

        return@async checkAllSignatures(boundWitness.signingData, boundWitness).await()
    }

    /**
     * Loops through the signatures and checks each signature.
     *
     * @param signingData The data signed from the bound witness.
     * @param boundWitness The bound witness containing the signatures to check.
     * @return Will return the validity if the bound witness in terms of signatures.
     */
    private fun checkAllSignatures(signingData: ByteArray, boundWitness: XyoBoundWitness) : Deferred<Boolean> = GlobalScope.async {
        for (partyNum in 0 until (boundWitness.numberOfParties ?: 0) ) {
            val keySet = boundWitness.getFetterOfParty(partyNum)?.get(XyoSchemas.KEY_SET.id)?.getOrNull(0) as? XyoIterableObject
            val sigSet = boundWitness.getWitnessOfParty(partyNum)?.get(XyoSchemas.SIGNATURE_SET.id)?.getOrNull(0) as? XyoIterableObject

            if (!(keySet is XyoIterableObject
                            && sigSet is XyoIterableObject
                            && checkSinglePartySignatures(keySet, sigSet, signingData).await())) {

                return@async false
            }
        }

        return@async true
    }

    /**
     * Checks a single parties signatures.
     *
     * @param keys The key set of the party
     * @param signatures The signature set of the party
     * @param signingData The data signed in the bound witness.
     * @return The validity of the signatures
     */
    private fun checkSinglePartySignatures (keys: XyoIterableObject, signatures : XyoIterableObject, signingData : ByteArray) : Deferred<Boolean> = GlobalScope.async {
        for (keyNum in 0 until keys.count) {
            val key = keys[keyNum]
            val signature = signatures[keyNum]

            val verify = XyoSigner.verify(key, signature, signingData).await()
            
            if ((verify == null && !allowUnknown) || verify == false) {
                return@async false
            }
        }

        return@async true
    }
}