package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.nio.ByteBuffer

/**
 * A Xyo Bound Witness Object
 */

abstract class XyoBoundWitness : XyoIterableObject() {

    /**
     * If the bound witness is completed or not.
     */
    val completed: Boolean
        get() {
            if ((this[XyoSchemas.FETTER.id].size == this[XyoSchemas.WITNESSS.id].size)
                    && this[XyoSchemas.WITNESSS.id].isNotEmpty()) {
                return true
            }
            return false
        }

    override val schema: XyoObjectSchema = XyoSchemas.BW

    /**
     * Gets the hash of the bound witness.
     *
     * @param hashCreator A hash provider to create the hash with.
     * @return A deferred XyoHash
     */
    fun getHash(hashCreator: XyoHash.XyoHashProvider) = GlobalScope.async {
        val dataToHash = getSigningData()
        return@async hashCreator.createHash(dataToHash).await()
    }

    /**
     * Creates a signature of the bound witness.
     *
     * @param signer A signer to sign with.
     * @return A deferred XyoObject (signature).
     */
    fun signCurrent(signer: XyoSigner) : Deferred<XyoBuff> = GlobalScope.async {
        val dataToSign = getSigningData()
        return@async signer.signData(dataToSign).await()
    }

    fun getSigningData(): ByteArray {
        // todo
        return byteArrayOf(0xFF.toByte())
    }

    companion object : XyoFromSelf {

        override fun getInstance(byteArray: ByteArray): XyoBoundWitness {
            return object : XyoBoundWitness() {
                override val allowedOffset: Int = 0
                override var item: ByteArray = byteArray

            }
        }

        /**
         * Will get the number of parties in a bound witness.
         *
         * @param boundWitness The boundWitness to check
         * @return The number of parties, if null, there is a inconsistent amount of parties.
         */
        fun getNumberOfParties (boundWitness: XyoBoundWitness) : Int? {
//            val keySetNumber = boundWitness.publicKeys.count
//            val payloadNumber = boundWitness.payloads.count
//            val signatureNumber = boundWitness.signatures.count
//
//            if (keySetNumber == payloadNumber &&  keySetNumber == signatureNumber) {
//                return keySetNumber
//            }
//
//            return null
            return 2
        }
    }
}
