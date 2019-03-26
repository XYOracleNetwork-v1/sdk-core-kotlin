package network.xyo.sdkcorekotlin.persist

import kotlinx.coroutines.Deferred

/**
 * A XyoStorageProvider is meant to provide a persistence layer. It abstracts exactly
 * how that is done. This could be in-memory persist or some disk strategy. It aims to persist data
 * in anon-volatile way.
 */
interface XyoStorageProvider {
    /**
     * Writes to the persist.
     *
     * @param key A key so that data can be received at a future point.
     * @param value The value that is held under a key.
     * @throws XyoStorageException if there is an error writing.
     */
    @Throws(XyoStorageException::class)
    fun write(key: ByteArray, value: ByteArray) : Deferred<Unit>

    /**
     * Read from persist.
     *
     * @param key A key to retrieve the data from. This key is set from write()
     * @return Returns a deferred that contains a ByteArray. Contains the value of
     * the key. If the key does it exist, the value will be null.
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    fun read(key: ByteArray): Deferred<ByteArray?>

    /**
     * The provider returns all the corresponding keys for the values stored.
     *
     * @return Returns a deferred Array<ByteArray>. The Deferred contains an array of ByteArrays that
     * are keys.
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    fun getAllKeys(): Deferred<Iterator<ByteArray>>

    /**
     * Deletes the value for the corresponding key.
     *
     * @param key A key to delete the data from. This key is set from write()
     * @throws XyoStorageException if there is an error deleting.
     */
    @Throws(XyoStorageException::class)
    fun delete(key: ByteArray) : Deferred<Unit>

    /**
     * Checks if a key exists in persist.
     *
     * @return Returns a deferred Boolean, contains a boolean. If the persist
     * provider contains the key, this will be null, and vice versa.
     * @throws XyoStorageException if there is an error reading.
     */
    @Throws(XyoStorageException::class)
    fun containsKey(key: ByteArray): Deferred<Boolean>
}
