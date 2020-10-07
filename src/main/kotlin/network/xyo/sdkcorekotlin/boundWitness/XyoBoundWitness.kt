package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure


/**
 * A Bound Witness Object that is independent of origin state. This implements the cryptographic structure discussed
 * in the XYO Network Yellow Paper, and White Paper.
 */
@ExperimentalStdlibApi
open class XyoBoundWitness(byteArray: ByteArray = startingBytes, offset: Int = 0) : XyoIterableStructure(byteArray, offset) {

    /**
     * If the bound witness is completed or not. This is represented by the number of fetters and the number of
     * witnesses and not 0.
     */
    val completed: Boolean
        get() {
            if ((this[XyoSchemas.FETTER.id].size == this[XyoSchemas.WITNESS.id].size)
                    && this[XyoSchemas.WITNESS.id].isNotEmpty()) {
                return true
            }
            return false
        }

    /**
     * Gets the offset of the boundary to read for signing. This is where the fetters meet the witnesses.
     */
    private val witnessFetterBoundary: Int
        get() {
            val fetters = this[XyoSchemas.FETTER.id]
            var offsetSize = 0

            for (fetter in fetters) {
                offsetSize += fetter.sizeBytes + 2
            }

            return offsetSize
        }

    /**
     * Gets the number of parties in the bound witness
     */
    val numberOfParties : Int?
        get() = getNumberOfParties(this)

    /**
     * Gets the number of fetters in the current bound witness.
     */
    protected val numberOfFetters : Int
        get() {
            return this[XyoSchemas.FETTER.id].size
        }

    /**
     * Gets the number of witnesses in the current bound witness.
     */
    protected val numberOfWitnesses : Int
        get() {
            return this[XyoSchemas.WITNESS.id].size
        }

    /**
     * Gets a fetter from a party in a bound witness.
     *
     * @param partyNum The index of the party in the bound witness.
     * @return The party's fetter. Will return null if out of index.
     */
    fun getFetterOfParty(partyNum : Int) : XyoIterableStructure? {
        val numOfParties = numberOfParties ?: return null

        if (numOfParties <= partyNum) {
            return null
        }

        return getBoundWitnessItemAtIndex(partyNum)
    }

    /**
     * Gets a witness from a party in a bound witness.
     *
     * @param partyNum The index of the party in the bound witness.
     * @return The party's witness. Will return null if out of index.
     */     
    fun getWitnessOfParty(partyNum: Int) : XyoIterableStructure? {
        val numOfParties = numberOfParties ?: return null

        if (numOfParties <= partyNum) {
            return null
        }

        return getBoundWitnessItemAtIndex((numOfParties * 2) - (partyNum + 1))
    }

    /**
     * Gets party information for an index (should only be fetters or witnesses)
     *
     * @param posIndex The index to grab from.
     * @return Will return null if the pos index is out of range or if the bound witness is incomplete.
     */
    fun getBoundWitnessItemAtIndex (posIndex : Int) : XyoIterableStructure? {
        if (completed) {
            return this[posIndex] as? XyoIterableStructure
        }

        return null
    }

    /**
     * Gets the signing data of the bound witness. This is all of the fetters.
     */

    internal val signingData : ByteArray
        get() = valueCopy.copyOfRange(0, witnessFetterBoundary)

    /**
     * Creates a fetter with the given payload and publicKeys.
     *
     * @param payload The payloads to add to the fetter.
     * @param publicKeys The public keys to add to the fetter. This type should be a key set.
     * @return The newly created fetter.
     */
    protected fun createFetter (payload: Array<XyoObjectStructure>, publicKeys : XyoObjectStructure) : XyoObjectStructure {
        val itemsInFetter = ArrayList<XyoObjectStructure>()
        itemsInFetter.add(publicKeys)
        itemsInFetter.addAll(payload)
        return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.FETTER, itemsInFetter.toTypedArray())
    }

    /**
     * Creates a witness with the given payload and signatures.
     *
     * @param payload The payloads to add to the witness.
     * @param signatures The public keys to add to the witness. This type should be a signature set.
     * @return The newly created witness.
     */
    protected fun createWitness (payload: Array<XyoObjectStructure>, signatures : XyoObjectStructure) : XyoIterableStructure {
        val itemsInWitness = ArrayList<XyoObjectStructure>()
        itemsInWitness.add(signatures)
        itemsInWitness.addAll(payload)
        return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.WITNESS, itemsInWitness.toTypedArray())
    }

    /**
     * Gets the hash of the bound witness.
     *
     * @param hashCreator A hash provider to create the hash with.
     * @return A deferred XyoHash
     */
    suspend fun getHash(hashCreator: XyoHash.XyoHashProvider): XyoHash {
        return hashCreator.createHash(signingData)
    }

    /**
     * Creates a signature of the bound witness.
     *
     * @param signer A signer to sign with.
     * @return A deferred XyoObject (signature).
     */
    suspend fun signCurrent(signer: XyoSigner) : XyoObjectStructure {
        return signer.signData(signingData)
    }


    companion object : XyoInterpret {

        /**
        * Gets bytes from an untyped bound witness
        */

        val startingBytes = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.BW, arrayOf()).bytesCopy

        /**
         * Gets a new instance of the bound witness from bytes.
         *
         * @param byteArray The byes of the bound witness to create.
         * @return The XyoBuff bound witness.
         */
        override fun getInstance(byteArray: ByteArray): XyoBoundWitness {
            return XyoBoundWitness(byteArray, 0)
        }

        /**
         * Will get the number of parties in a bound witness.
         *
         * @param boundWitness The boundWitness to check
         * @return The number of parties, if null, there is a inconsistent amount of parties.
         */
        fun getNumberOfParties (boundWitness: XyoBoundWitness) : Int? {
            val numberOfFetters = boundWitness[XyoSchemas.FETTER.id].size
            val numberOfWitnesses= boundWitness[XyoSchemas.WITNESS.id].size

            if (numberOfFetters == numberOfWitnesses) {
                return numberOfFetters
            }

            return null
        }
    }
}
