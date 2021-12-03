package network.xyo.sdkcorekotlin.persist

/**
 * A XyoKeyValueStore is meant to provide a persistence layer. It abstracts exactly
 * how that is done. This could be in-memory persist or some disk strategy. It aims to persist data
 * in a non-volatile way.
 */
interface XyoKeyValueStore {
    /**
     * Writes to the persist.
     *
     * @param key A key so that data can be received at a future point.
     * @param value The value that is held under a key.
     * @throws XyoStorageException if there is an error writing.
     */
    @Throws(XyoStorageException::class)
    suspend fun write(key: ByteArray, value: ByteArray)

    /**
     * Read from persist.
     *
     * @param key A key to retrieve the data from. This key is set from write()
     * @return Returns a deferred that contains a ByteArray. Contains the value of
     * the key. If the key does it exist, the value will be null.
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    suspend fun read(key: ByteArray): ByteArray?

    /**
     * The provider returns all the corresponding keys for the values stored.
     *
     * @return Returns a deferred Array<ByteArray>. The Deferred contains an array of ByteArrays that
     * are keys.
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    suspend fun getAllKeys(): Iterator<ByteArray>

    /**
     * Deletes the value for the corresponding key.
     *
     * @param key A key to delete the data from. This key is set from write()
     * @throws XyoStorageException if there is an error deleting.
     */
    @Throws(XyoStorageException::class)
    suspend fun delete(key: ByteArray)

    /**
     * Checks if a key exists in persist.
     *
     * @return Returns a deferred Boolean, contains a boolean. If the persist
     * provider contains the key, this will be null, and vice versa.
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    suspend fun containsKey(key: ByteArray): Boolean
}
