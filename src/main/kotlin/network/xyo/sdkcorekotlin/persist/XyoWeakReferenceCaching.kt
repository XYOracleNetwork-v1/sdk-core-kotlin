package network.xyo.sdkcorekotlin.persist

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

/**
 * A XyoStorageProvider that uses weak references to add a caching layer.
 *
 * @property layerToAddCacheTo The layer to cache on top of.
 */
class XyoWeakReferenceCaching (private val layerToAddCacheTo : XyoStorageProvider) : XyoStorageProvider {
    private val cache = WeakHashMap<Int, ByteArray>()

    override fun write(key: ByteArray, value: ByteArray) : Deferred<Unit> = GlobalScope.async {
            cache[key.contentHashCode()] = value
            layerToAddCacheTo.write(key, value).await()
    }

    override fun read(key: ByteArray): Deferred<ByteArray?> = GlobalScope.async {
        val cachedValue = cache[key.contentHashCode()]
        if (cachedValue != null) {
            return@async cachedValue
        }
        return@async layerToAddCacheTo.read(key).await()
    }

    override fun containsKey(key: ByteArray): Deferred<Boolean> = GlobalScope.async {
        if (cache.containsKey(key.contentHashCode())) {
            return@async true
        }

        return@async layerToAddCacheTo.containsKey(key).await()
    }

    override fun delete(key: ByteArray): Deferred<Unit> = GlobalScope.async {
        cache.remove(key.contentHashCode())
        layerToAddCacheTo.delete(key).await()
    }

    override fun getAllKeys(): Deferred<Iterator<ByteArray>> = GlobalScope.async {
        return@async layerToAddCacheTo.getAllKeys().await()
    }
}