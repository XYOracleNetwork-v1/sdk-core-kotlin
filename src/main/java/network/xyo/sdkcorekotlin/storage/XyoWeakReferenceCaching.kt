package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.*

/**
 * A XyoStorageProviderInterface that uses weak references to add a caching layer.
 *
 * @param layerToAddCacheTo The layer to cache on top of.
 */
class XyoWeakReferenceCaching (private val layerToAddCacheTo : XyoStorageProviderInterface) : XyoStorageProviderInterface {
    private val cache = WeakHashMap<Int, ByteArray>()

    override fun write(key: ByteArray, value: ByteArray): Deferred<Exception?> = async {
        try {
            cache[key.contentHashCode()] = value
            layerToAddCacheTo.write(key, value).await()
            return@async null
        } catch (exception : Exception) {
            return@async exception
        }
    }

    override fun read(key: ByteArray): Deferred<ByteArray?> = async {
        try {
            val cachedValue = cache[key.contentHashCode()]
            if (cachedValue != null) {
                return@async cachedValue
            }
            return@async layerToAddCacheTo.read(key).await()
        } catch (exception : Exception) {
            return@async null
        }
    }

    override fun containsKey(key: ByteArray): Deferred<Boolean> = async {
        if (cache.containsKey(key.contentHashCode())) {
            return@async true
        }

        return@async layerToAddCacheTo.containsKey(key).await()
    }

    override fun delete(key: ByteArray): Deferred<Exception?> = async {
        try {
            cache.remove(key.contentHashCode())
            return@async layerToAddCacheTo.delete(key).await()
        } catch (exception : Exception) {
            return@async exception
        }
    }

    override fun getAllKeys(): Deferred<Array<ByteArray>> = async {
        return@async layerToAddCacheTo.getAllKeys().await()
    }
}