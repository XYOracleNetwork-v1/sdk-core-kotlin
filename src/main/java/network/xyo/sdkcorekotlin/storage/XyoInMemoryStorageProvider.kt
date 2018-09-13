package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.async

/**
 * A simple in-memory storage implementation of the XyoStorageProviderInterface.
 */
class XyoInMemoryStorageProvider : XyoStorageProviderInterface {
    private val keys = ArrayList<ByteArray>()
    private val storageHashMap = HashMap<Int, ByteArray>()

    override fun containsKey(key: ByteArray) = async {
        return@async storageHashMap.containsKey(key.contentHashCode())
    }

    override fun delete(key: ByteArray) = async {
        storageHashMap.remove(key.contentHashCode())
        keys.remove(key)
    }

    override fun getAllKeys() = async {
        return@async keys.toTypedArray()
    }

    override fun read(key: ByteArray, timeout: Int) = async {
        return@async storageHashMap[key.contentHashCode()]
    }

    override fun write(key: ByteArray, value: ByteArray, priority: XyoStorageProviderPriority, cache: Boolean, timeout: Int) = async {
        keys.add(key)
        storageHashMap[key.contentHashCode()] = value
    }
}