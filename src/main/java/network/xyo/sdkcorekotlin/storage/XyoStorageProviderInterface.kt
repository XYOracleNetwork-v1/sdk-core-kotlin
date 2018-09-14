package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.Deferred

/**
 * A XyoStorageProviderInterface is meant to provide a persistence layer. It abstracts exactly
 * how that is done. This could be in-memory storage or some disk strategy. It aims to persist data
 * in anon-volatile way.
 */
interface XyoStorageProviderInterface {
    /**
     * Writes to the storage.
     *
     * @param key A key so that data can be received at a future point.
     * @param value The value that is held under a key.
     * @return A deferred Exception?. If the error is null. The operation was successful.
     */
    fun write(key: ByteArray, value: ByteArray) : Deferred<Exception?>

    /**
     * Read from storage.
     *
     * @param key A key to retrieve the data from. This key is set from write()
     * @return Returns a deferred that contains a ByteArray. Contains the value of
     * the key. If the key does it exist, the value will be null.
     */
    fun read(key: ByteArray): Deferred<ByteArray?>

    /**
     * The provider returns all the corresponding keys for the values stored.
     *
     * @return Returns a deferred Array<ByteArray>. The Deferred contains an array of ByteArrays that
     * are keys.
     */
    fun getAllKeys(): Deferred<Array<ByteArray>>

    /**
     * Deletes the value for the corresponding key.
     *
     * @param key A key to delete the data from. This key is set from write()
     */
    fun delete(key: ByteArray) : Deferred<Exception?>

    /**
     * Checks if a key exists in storage.
     *
     * @return Returns a deferred Boolean, contains a boolean. If the storage
     * provider contains the key, this will be null, and vice versa.
     */
    fun containsKey(key: ByteArray): Deferred<Boolean>
}
