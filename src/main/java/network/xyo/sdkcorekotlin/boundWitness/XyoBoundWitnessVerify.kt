package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigner

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

        val numberOfParties = XyoBoundWitness.getNumberOfParties(boundWitness)
        val dataSignedOn = boundWitness.getSigningData()
        val publicKeys = boundWitness.publicKeys
        val signatures = boundWitness.signatures

        if (numberOfParties != null) {
            return@async checkAllSignatures(dataSignedOn, numberOfParties, publicKeys, signatures).await()
        }

        return@async false
    }

    private fun checkAllSignatures(signingData: ByteArray, numberOfParties : Int, publicKeys: Array<XyoKeySet>, signatures: Array<XyoSignatureSet>) : Deferred<Boolean?> = GlobalScope.async {
        for (partyNum in 0 until numberOfParties) {
            val keys = publicKeys[partyNum].array
            val sigs = signatures[partyNum].array

            if (keys.size != sigs.size) {
                return@async false
            }

            val isSigValid = checkSinglePartySignatures(keys, sigs, signingData).await()

            if (!isSigValid) {
                return@async false
            }
        }

        return@async true
    }


    private fun checkSinglePartySignatures (keys: Array<XyoObject>, signatures : Array<XyoObject>, signingData : ByteArray) : Deferred<Boolean> = GlobalScope.async {
        for (keyNum in 0 until keys.size) {
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