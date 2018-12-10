package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.toHexString


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
            println("d")
            return@async false
        }

        val dataSignedOn = boundWitness.signingData
        return@async checkAllSignatures(dataSignedOn, boundWitness).await()
    }

    private fun checkAllSignatures(signingData: ByteArray, boundWitness: XyoBoundWitness) : Deferred<Boolean?> = GlobalScope.async {
        for (partyNum in 0 until (boundWitness.numberOfParties ?: 0) ) {
            val keySet = boundWitness.getFetterOfParty(partyNum)?.get(XyoSchemas.KEY_SET.id)?.getOrNull(0) as? XyoIterableObject
            val sigSet = boundWitness.getWitnessOfParty(partyNum)?.get(XyoSchemas.SIGNATURE_SET.id)?.getOrNull(0) as? XyoIterableObject

            if (keySet is XyoIterableObject && sigSet is XyoIterableObject) {
                val isSigValid = checkSinglePartySignatures(keySet, sigSet, signingData).await()

                if (!isSigValid) {
                    return@async false
                }
            } else {
                return@async false
            }
        }


        return@async true
    }

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