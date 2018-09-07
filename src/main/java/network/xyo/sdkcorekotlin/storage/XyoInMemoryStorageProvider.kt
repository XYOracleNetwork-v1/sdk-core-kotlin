package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoResult

/**
 * A simple in-memory storage implementation of the XyoStorageProviderInterface.
 */
class XyoInMemoryStorageProvider : XyoStorageProviderInterface {
    private val keys = ArrayList<ByteArray>()
    private val storageHashMap = HashMap<Int, ByteArray>()

    override fun containsKey(key: ByteArray) = async {
        return@async XyoResult(storageHashMap.containsKey(key.contentHashCode()))
    }

    override fun delete(key: ByteArray) = async {
        storageHashMap.remove(key.contentHashCode())
        keys.remove(key)
        return@async null
    }

    override fun getAllKeys() = async {
        return@async XyoResult(keys.toTypedArray())
    }

    override fun read(key: ByteArray, timeout: Int) = async {
        return@async XyoResult<ByteArray?>(storageHashMap[key.contentHashCode()])
    }

    override fun write(key: ByteArray, value: ByteArray, priority: XyoStorageProviderPriority, cache: Boolean, timeout: Int) = async {
        keys.add(key)
        storageHashMap[key.contentHashCode()] = value
        return@async null
    }
}