package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.queries.XyoGetOriginBlockByHash
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff


interface XyoOriginBlockRepository : XyoGetOriginBlockByHash {
    /**
     * Removes an origin block from the navigator and from storage
     *
     * @param originBlockHash the hash of the origin block to be removed.
     * @return A deferred XyoError, if the error is null, the operation was successful.
     */
    fun removeOriginBlock (originBlockHash : XyoBuff) : Deferred<Exception?>

    /**
     * Checks if an origin blocks exists in storage.
     *
     * @param originBlockHash the hash of the origin block to check.
     * @return A deferred Boolean
     */
    fun containsOriginBlock (originBlockHash: XyoBuff) : Deferred<Boolean?>

    /**
     * Gets all of the origin blocks in storage.
     *
     * @return A deferred Array<ByteArray>
     */
    fun getAllOriginBlockHashes () : Deferred<Iterator<XyoBuff>?>

    /**
     * Adds a bound bound witness to the navigator and stores it. If the bound witness is not an
     * origin block, it will return an error.
     *
     * @param originBlock The bound witness to add to the navigator.
     */
    fun addBoundWitness (originBlock : XyoBoundWitness) : Deferred<Exception?>
}