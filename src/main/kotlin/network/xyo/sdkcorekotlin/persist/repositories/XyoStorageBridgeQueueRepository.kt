package network.xyo.sdkcorekotlin.persist.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.node.XyoBridgeQueueItem
import network.xyo.sdkcorekotlin.persist.XyoKeyValueStore
import network.xyo.sdkcorekotlin.repositories.XyoBridgeQueueRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.lang.Math.min
import java.nio.ByteBuffer

/**
 * A class to persist storage for bridge queues.
 *
 */

class XyoStorageBridgeQueueRepository (private val store: XyoKeyValueStore) : XyoBridgeQueueRepository {
    private var queueCache = ArrayList<XyoBridgeQueueItem>()

    override fun getQueue (): Array<XyoBridgeQueueItem> {
        return queueCache.toTypedArray()
    }

    override fun setQueue (queue:  Array<XyoBridgeQueueItem>) {
        queueCache = ArrayList(queue.asList())
    }

    override fun addQueueItem (item: XyoBridgeQueueItem) {
        val insertIndex = getInsertIndex(item.weight)
        queueCache.add(insertIndex, item)
    }

    override fun removeQueueItems (items: Array<XyoObjectStructure>) {
        for (item in items) {
            queueCache.removeIf { cachedItem ->
                return@removeIf item.bytesCopy.contentEquals(cachedItem.hash.bytesCopy)
            }
        }
    }

    override fun getLowestWeight (n: Int): Array<XyoBridgeQueueItem> {
        if (queueCache.size == 0 || n == 0) {
            return arrayOf()
        }

        val itemsToReturn = ArrayList<XyoBridgeQueueItem>()

        for (i in 0..min(n - 1, queueCache.size - 1)) {
            itemsToReturn.add(queueCache[i])
        }

        return itemsToReturn.toTypedArray()
    }

    override fun incrementWeights (hashes: Array<XyoObjectStructure>) {
        for (hash in hashes) {
            val indexToAdd = queueCache.indexOfFirst { cachedItem ->
                return@indexOfFirst cachedItem.hash.bytesCopy.contentEquals(hash.bytesCopy)
            }

            queueCache[indexToAdd].weight += 1
        }
    }

    override fun commit () : Deferred<Unit> = GlobalScope.async {
        val encodedQueueItems: Array<XyoObjectStructure> = Array(queueCache.size) { i ->
            return@Array this@XyoStorageBridgeQueueRepository.queueCache[i].encode()
        }

        val encodedMaster = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, encodedQueueItems)
        store.write(STORE_QUEUE_KEY, encodedMaster.bytesCopy).await()
    }

    @Suppress("unused")
    fun restore () : Deferred<Unit> = GlobalScope.async {
        val encodedItems = store.read(STORE_QUEUE_KEY).await() ?: return@async
        val restoredQueueCache = ArrayList<XyoBridgeQueueItem>()
        val it = XyoIterableStructure(encodedItems, 0).iterator

        while (it.hasNext()) {
            val item = it.next() as? XyoIterableStructure

            if (item != null) {
                restoredQueueCache.add(decodeBridgeItem(item))
            }
        }

        queueCache = restoredQueueCache
    }

    private fun getInsertIndex (weight: Int): Int {
        if (queueCache.size == 0) {
            return 0
        }

        for (i in 0..queueCache.size) {
            if (queueCache[i].weight >= weight) {
                return i
            }
        }

        return 0

    }

    private fun XyoBridgeQueueItem.encode() : XyoIterableStructure {
        val hashStructure = this.hash
        val weightStructure = XyoObjectStructure.newInstance(XyoSchemas.BLOB, ByteBuffer.allocate(4).putInt(this.weight).array())

        return XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf(hashStructure, weightStructure))
    }

    private fun decodeBridgeItem (item : XyoIterableStructure): XyoBridgeQueueItem {
        val hash = item[0]
        val weight = ByteBuffer.wrap(item[1].valueCopy).int

        return XyoBridgeQueueItem(weight, hash)
    }

    companion object {
        private val STORE_QUEUE_KEY = "STORE_QUEUE_KEY".toByteArray()
    }
}