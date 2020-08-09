package network.xyo.sdkcorekotlin.persist

import java.util.*

/**
 * A XyoKeyValueStore that uses weak references to add a caching layer.
 *
 * @property layerToAddCacheTo The layer to cache on top of.
 */
class XyoWeakReferenceCaching (private val layerToAddCacheTo : XyoKeyValueStore) : XyoKeyValueStore {
    private val cache = WeakHashMap<Int, ByteArray>()

    override suspend fun write(key: ByteArray, value: ByteArray) {
            cache[key.contentHashCode()] = value
            layerToAddCacheTo.write(key, value)
    }

    override suspend fun read(key: ByteArray): ByteArray? {
        val cachedValue = cache[key.contentHashCode()]
        if (cachedValue != null) {
            return cachedValue
        }
        return layerToAddCacheTo.read(key)
    }

    override suspend fun containsKey(key: ByteArray): Boolean {
        if (cache.containsKey(key.contentHashCode())) {
            return true
        }

        return layerToAddCacheTo.containsKey(key)
    }

    override suspend fun delete(key: ByteArray) {
        cache.remove(key.contentHashCode())
        layerToAddCacheTo.delete(key)
    }

    override suspend fun getAllKeys(): Iterator<ByteArray> {
        return layerToAddCacheTo.getAllKeys()
    }
}