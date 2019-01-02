package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.queries.XyoCurrentState
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner


/**
 * This interface is used to keep track of the state when creating an origin chain. This includes
 * the previous hash, index, and the current/next key-pairs to sign with.
 */
interface XyoOriginStateRepository : XyoCurrentState {
    /**
     * This function should be called whenever a new block is added to a origin chain.
     *
     * @param hash the hash of the origin block just created.
     */
    fun newOriginBlock (hash : XyoHash)

    /**
     * Removes the oldest signer so that a party can rotate keys when creating an origin chain.
     */
    fun removeOldestSigner ()

    /**
     * Adds a signer to the queue to be used in the origin chain.
     *
     * @param signer The signer to be added to the queue.
     */
    fun addSigner (signer : XyoSigner)
}