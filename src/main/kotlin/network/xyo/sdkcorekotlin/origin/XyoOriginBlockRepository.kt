package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.exceptions.XyoStorageException
import network.xyo.sdkcorekotlin.queries.XyoGetOriginBlockByHash
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff


interface XyoOriginBlockRepository : XyoGetOriginBlockByHash {
    /**
     * Removes an origin block from the navigator and from storage
     *
     * @param originBlockHash the hash of the origin block to be removed.
     * @return A deferred XyoError, if the error is null, the operation was successful.
     * @throws XyoStorageException if there is an error deleting.
     */
    @Throws(XyoStorageException::class)
    fun removeOriginBlock (originBlockHash : XyoBuff) : Deferred<Unit>

    /**
     * Checks if an origin blocks exists in storage.
     *
     * @param originBlockHash the hash of the origin block to check.
     * @return A deferred Boolean
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    fun containsOriginBlock (originBlockHash: XyoBuff) : Deferred<Boolean?>

    /**
     * Gets all of the origin blocks in storage.
     *
     * @return A deferred Array<ByteArray>
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    fun getAllOriginBlockHashes () : Deferred<Iterator<XyoBuff>?>

    /**
     * Adds a bound bound witness to the navigator and stores it. If the bound witness is not an
     * origin block, it will return an error.
     *
     * @param originBlock The bound witness to add to the navigator.
     * @throws XyoStorageException if there is an error writing.
     */
    @Throws(XyoStorageException::class)
    fun addBoundWitness (originBlock : XyoBoundWitness) : Deferred<Unit>
}