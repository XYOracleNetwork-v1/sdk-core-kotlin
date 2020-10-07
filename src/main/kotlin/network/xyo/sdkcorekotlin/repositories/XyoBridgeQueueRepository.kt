package network.xyo.sdkcorekotlin.repositories

import network.xyo.sdkcorekotlin.node.XyoBridgeQueueItem
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * An interface for bridge queue storage.
 */
@ExperimentalStdlibApi
interface XyoBridgeQueueRepository {
    fun getQueue (): Array<XyoBridgeQueueItem>
    fun setQueue (queue:  Array<XyoBridgeQueueItem>)
    fun addQueueItem (item: XyoBridgeQueueItem)
    fun removeQueueItems (items: Array<XyoObjectStructure>)
    fun getLowestWeight (n: Int): Array<XyoBridgeQueueItem>
    fun incrementWeights (hashes: Array<XyoObjectStructure>)
    suspend fun commit ()
}