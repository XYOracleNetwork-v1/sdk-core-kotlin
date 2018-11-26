package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.ARRAY_TYPED
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.ARRAY_UNTYPED
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.PAYLOAD
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.nio.ByteBuffer

/**
 * A Xyo Bound Witness Object
 */

abstract class XyoBoundWitness : XyoInterpreter {
    /**
     * All of the public keys in the bound witness.
     */
    abstract val publicKeys: ByteArray

    /**
     * All of the payloads in the bound witness
     */
    abstract val payloads: ByteArray

    /**
     * All of the signaturePacking in the bound witness.
     */
    abstract val signatures: ByteArray

    /**
     * If the bound witness is completed or not.
     */
    val completed: Boolean
        get() {
            if ((XyoIterableObject(publicKeys).size == XyoIterableObject(signatures).size)
                    && (XyoIterableObject(signatures).size == XyoIterableObject(payloads).size)
                    && XyoIterableObject(publicKeys).size != 0) {
                return true
            }
            return false
        }

    override val self: ByteArray
        get() = XyoObjectSetCreator.createUntypedIterableObject(
                schema,
                arrayOf(publicKeys, payloads, signatures)
        )

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
    fun signCurrent(signer: XyoSigner) : Deferred<ByteArray> = GlobalScope.async {
        val dataToSign = getSigningData()
        return@async signer.signData(dataToSign).await()
    }

    fun getSigningData(): ByteArray {
        val signedPayloadsToSign = ArrayList<ByteArray>()
        var signingDataSize = 0

        for (payload in XyoIterableObject(payloads).iterator) {
            val signedPayload = XyoIterableObject((payload))[0]
            signingDataSize += signedPayload.size
            signedPayloadsToSign.add(signedPayload)
        }

        signingDataSize += publicKeys.size

        val buffer = ByteBuffer.allocate(signingDataSize)

        buffer.put(publicKeys)

        for (signedPayload in signedPayloadsToSign) {
            buffer.put(signedPayload)
        }

        return buffer.array()
    }

    companion object : XyoFromSelf {

        override fun getInstance(byteArray: ByteArray): XyoBoundWitness {
            return object : XyoBoundWitness() {
                override val self: ByteArray
                    get() = byteArray

                override val publicKeys: ByteArray
                    get() {
                        return XyoIterableObject(self)[0]
                    }

                override val payloads: ByteArray
                    get() {
                        return XyoIterableObject(self)[1]
                    }

                override val signatures: ByteArray
                    get() {
                        return XyoIterableObject(self)[2]
                    }
            }
        }

        /**
         * Will get the number of parties in a bound witness.
         *
         * @param boundWitness The boundWitness to check
         * @return The number of parties, if null, there is a inconsistent amount of parties.
         */
        fun getNumberOfParties (boundWitness: XyoBoundWitness) : Int? {
            val keySetNumber = XyoIterableObject(boundWitness.publicKeys).size
            val payloadNumber = XyoIterableObject(boundWitness.payloads).size
            val signatureNumber = XyoIterableObject(boundWitness.signatures).size

            if (keySetNumber == payloadNumber &&  keySetNumber == signatureNumber) {
                return keySetNumber
            }

            return null
        }
    }
}
