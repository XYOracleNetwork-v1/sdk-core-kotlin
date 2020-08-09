package network.xyo.sdkcorekotlin.persist

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.ConcurrentHashMap

/**
 * A simple in-memory persist implementation of the XyoKeyValueStore.
 */
class XyoInMemoryStorageProvider : XyoKeyValueStore {
    private val keys = ArrayList<ByteArray>()
    private val storageHashMap = ConcurrentHashMap<Int, ByteArray>()

    override suspend fun containsKey(key: ByteArray): Boolean {
        return storageHashMap.containsKey(key.contentHashCode())
    }

    override suspend fun delete(key: ByteArray) {
        storageHashMap.remove(key.contentHashCode())
        keys.remove(key)
        return
    }

    override suspend fun getAllKeys(): Iterator<ByteArray> {
        return keys.iterator()
    }

    override suspend fun read(key: ByteArray): ByteArray? {
        return storageHashMap[key.contentHashCode()]
    }

    override suspend fun write(key: ByteArray, value: ByteArray) {
        keys.add(key)
        storageHashMap[key.contentHashCode()] = value
        return
    }
}