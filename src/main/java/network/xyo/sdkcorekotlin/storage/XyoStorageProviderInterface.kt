package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult

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
     * @param priority The priory of the speed of the write.
     * @param cache If true, the XyoStorageProvider will cache the data, if false, the
     * XyoStorageProvider
     * will not cache the data.
     * @param timeout Specified in milliseconds. Defaults to 60000, or 1 minute if null. If timed
     * out the method will return ERR_TIMEOUT.
     * @return A deferred XyoError?. If the error is null. The operation was successful.
     */
    fun write(key: ByteArray, value: ByteArray, priority: XyoStorageProviderPriority, cache: Boolean, timeout: Int): Deferred<XyoError?>

    /**
     * Read from storage.
     *
     * @param key A key to retrieve the data from. This key is set from write()
     * @param timeout Specified in Milliseconds. Defaults to 60000, or 1 minute if null. If timed
     * out the method will have a ERR_TIMEOUT.
     * @return Returns a deferred that contains a XyoResult. The XyoResult contains the value of
     * the key. If the key does it exist, the value will be null. If XyoResult.error is null,
     * then the XyoResult.value is good to go.
     */
    fun read(key: ByteArray, timeout: Int): Deferred<XyoResult<ByteArray?>>

    /**
     * The provider returns all the corresponding keys for the values stored.
     *
     * @return Returns a deferred XyoResult. The XyoResult contains an array of ByteArrays that
     * are keys. If XyoResult.error is null, then the XyoResult.value is good to go.
     */
    fun getAllKeys(): Deferred<XyoResult<Array<ByteArray>>>

    /**
     * Deletes the value for the corresponding key.
     *
     * @param key A key to delete the data from. This key is set from write()
     * @return Returns deferred XyoError, if the XyoError isnull, the operation was successful.
     */
    fun delete(key: ByteArray): Deferred<XyoError?>

    /**
     * Checks if a key exists in storage.
     *
     * @return Returns a deferred XyoResult. The XyoResult contains a boolean. If the storage
     * provider contains the key, this will be null, and vice versa.
     */
    fun containsKey(key: ByteArray): Deferred<XyoResult<Boolean>>
}
