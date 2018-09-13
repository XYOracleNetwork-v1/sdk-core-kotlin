package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.signing.XyoSigner

/**
 * This class is used to keep track of the state when creating an origin chain. This includes
 * the previous hash, index, and the current/next key-pairs to sign with.
 *
 * @param indexOffset This value is used to create a state manager where the index does not doBoundWitness
 * at 0. This is used when re-starting a origin chain.
 */
class XyoOriginChainStateManager (private val indexOffset : Int) {
    private val currentSigners = ArrayList<XyoSigner>()
    private val waitingSigners = ArrayList<XyoSigner>()
    private var latestHash : XyoHash? = null

    /**
     * The total number of elements since creation, NOT including the indexOffset.
     * */
    var count = 0

    /**
     * All of the hashes in the origin chain.
     */
    val allHashes = ArrayList<XyoHash>()

    /**
     * All of the public keys used in the origin chain.
     */
    val allPublicKeys = ArrayList<XyoObject>()

    /**
     * The next public key to be used in the origin chain.
     */
    var nextPublicKey : XyoNextPublicKey? = null

    /**
     * The index of the origin chain.
     */
    val index : XyoIndex
        get() = XyoIndex(count + indexOffset)

    /**
     * The previous hash to be included in the next origin block.
     */
    val previousHash : XyoPreviousHash?
        get() {
            val latestHashValue = latestHash
            if (latestHashValue != null) {
                return XyoPreviousHash(latestHashValue)
            }
            return null
        }

    /**
     * Gets the all of signers to use when creating the next origin block.
     *
     * @return all of the signers.
     */
    fun getSigners () : Array<XyoSigner>{
        return currentSigners.toTypedArray()
    }

    /**
     * Adds a signer to the queue to be used in the origin chain.
     *
     * @param signer The signer to be added to the queue.
     */
    fun addSigner (signer : XyoSigner) {
        nextPublicKey = XyoNextPublicKey(signer.publicKey)
        waitingSigners.add(signer)
        allPublicKeys.add(signer.publicKey)
    }

    /**
     * Removes the oldest signer so that a party can rotate keys when creating an origin chain.
     */
    fun removeOldestSigner () {
        currentSigners.removeAt(0)
    }

    /**
     * This function should be called whenever a new block is added to a origin chain.
     *
     * @param hash the hash of the origin block just created.
     */
    fun newOriginBlock (hash : XyoHash) {
        nextPublicKey = null
        allHashes.add(hash)
        latestHash = hash
        count++
        addWaitingSigner()
    }

    private fun addWaitingSigner () {
        if (waitingSigners.size > 0) {
            currentSigners.add(waitingSigners.first())
            waitingSigners.removeAt(0)
        }
    }
}