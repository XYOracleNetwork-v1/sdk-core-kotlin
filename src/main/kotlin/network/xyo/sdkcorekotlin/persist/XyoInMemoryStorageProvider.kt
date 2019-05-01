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

    override fun containsKey(key: ByteArray) = GlobalScope.async {
        return@async storageHashMap.containsKey(key.contentHashCode())
    }

    override fun delete(key: ByteArray) = GlobalScope.async {
        storageHashMap.remove(key.contentHashCode())
        keys.remove(key)
        return@async
    }

    override fun getAllKeys() = GlobalScope.async {
        return@async keys.iterator()
    }

    override fun read(key: ByteArray) = GlobalScope.async {
        return@async storageHashMap[key.contentHashCode()]
    }

    override fun write(key: ByteArray, value: ByteArray) = GlobalScope.async {
        keys.add(key)
        storageHashMap[key.contentHashCode()] = value
        return@async
    }
}