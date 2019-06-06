package network.xyo.sdkcorekotlin.repositories

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.node.XyoBridgeQueueItem
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

interface XyoBridgeQueueRepository {
    fun getQueue (): Array<XyoBridgeQueueItem>
    fun setQueue (queue:  Array<XyoBridgeQueueItem>)
    fun addQueueItem (item: XyoBridgeQueueItem)
    fun removeQueueItems (items: Array<XyoObjectStructure>)
    fun getLowestWeight (n: Int): Array<XyoBridgeQueueItem>
    fun incrementWeights (hashes: Array<XyoObjectStructure>)
    fun commit () : Deferred<Unit>
}