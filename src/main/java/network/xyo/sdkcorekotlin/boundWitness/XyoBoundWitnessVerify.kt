package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject


class XyoBoundWitnessVerify (private val allowUnknown : Boolean) {
    /**
     * Will verify a single bound witness.
     *
     * @param boundWitness The bound witness to verify
     * @return If the bound witness was successful and null if does not have the capability to
     * verify (cant find verify method)
     */
    fun verify (boundWitness: XyoBoundWitness) : Deferred<Boolean?> = GlobalScope.async {
//        if (!boundWitness.completed) {
//            return@async false
//        }
//
//        val numberOfParties = XyoBoundWitness.getNumberOfParties(boundWitness)
//        val dataSignedOn = boundWitness.getSigningData()
//        val publicKeys = XyoIterableObject(boundWitness.publicKeys)
//        val signatures = XyoIterableObject(boundWitness.signatures)
//
//        if (numberOfParties != null) {
//            return@async checkAllSignatures(dataSignedOn, numberOfParties, publicKeys, signatures).await()
//        }

        return@async false
    }

    private fun checkAllSignatures(signingData: ByteArray, numberOfParties : Int, publicKeys: XyoIterableObject, signatures: XyoIterableObject) : Deferred<Boolean?> = GlobalScope.async {
//        for (partyNum in 0 until numberOfParties) {
//            val keys = XyoIterableObject(publicKeys[partyNum])
//            val sigs = XyoIterableObject(signatures[partyNum])
//
//            if (keys.size != sigs.size) {
//                return@async false
//            }
//
//            val isSigValid = checkSinglePartySignatures(keys, sigs, signingData).await()
//
//            if (!isSigValid) {
//                return@async false
//            }
//        }

        return@async true
    }


    private fun checkSinglePartySignatures (keys: XyoIterableObject, signatures : XyoIterableObject, signingData : ByteArray) : Deferred<Boolean> = GlobalScope.async {
//        for (keyNum in 0 until keys.size) {
//            val key = keys[keyNum]
//            val signature = signatures[keyNum]
//
//            // todo
//            // val verify = XyoSigner.verify(key, signature, signingData).await()
//
////            if ((verify == null && !allowUnknown) || verify == false) {
////                return@async false
////            }
//        }

        return@async true
    }
}