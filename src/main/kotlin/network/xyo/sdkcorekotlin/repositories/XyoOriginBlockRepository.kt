package network.xyo.sdkcorekotlin.repositories

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.persist.XyoStorageException
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * An interface that removes an origin block from the navigator and from persist, 
 * each method throws an XyoStorageException in the case of an error in operation.
 */
interface XyoOriginBlockRepository {
    /**
     * Removes an origin block from the navigator and from persist
     *
     * @param originBlockHash the hash of the origin block to be removed.
     * @return A deferred XyoError, if the error is null, the operation was successful.
     * @throws XyoStorageException if there is an error deleting.
     */
    @Throws(XyoStorageException::class)
    suspend fun removeOriginBlock (originBlockHash : XyoObjectStructure)

    /**
     * Checks if an origin blocks exists in persist.
     *
     * @param originBlockHash the hash of the origin block to check.
     * @return A deferred Boolean
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    suspend fun containsOriginBlock (originBlockHash: XyoObjectStructure) : Boolean

    /**
     * Gets all of the origin blocks in persist.
     *
     * @return A deferred Array<ByteArray>
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    suspend fun getAllOriginBlockHashes () : Iterator<XyoObjectStructure>?

    /**
     * Adds a bound witness to the navigator and stores it. If the bound witness is not an
     * origin block, it will return an error.
     *
     * @param originBlock The bound witness to add to the navigator.
     * @throws XyoStorageException if there is an error writing.
     */
    @Throws(XyoStorageException::class)
    suspend fun addBoundWitness (originBlock : XyoBoundWitness)

    @Throws(XyoStorageException::class)
    suspend fun getOriginBlockByBlockHash(originBlockHash: ByteArray): XyoBoundWitness?
}