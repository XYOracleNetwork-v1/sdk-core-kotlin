package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.repositories.XyoBridgeQueueRepository
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

/**
 * A class to manage outgoing origin blocks for bridges and sentinels.
 */
open class XyoBridgeQueue (val repo: XyoBridgeQueueRepository) {
    /**
     * The maximum number of blocks to send at a given time.
     */
    var sendLimit = 10

    /**
     * The point at witch blocks should be removed from the queue.
     */
    var removeWeight = 3

    /**
     * Adds an origin block into the bridge queue.
     *
     * @param blockHash The block to add.
     */
    fun addBlock (blockHash : XyoBuff) {
        addBlock(blockHash, 0)
    }

    /**
     * Adds an origin block into the bridge queue with a weight.
     *
     * @param blockHash The block to add.
     * @param weight The weight to add the block to the queue with.
     */
    fun addBlock (blockHash : XyoBuff, weight : Int) {
        val queueItem = XyoBridgeQueueItem(weight, blockHash)
        repo.addQueueItem(queueItem)
    }

    /**
     * Gets the current list of origin blocks to send.
     *
     * @return An array of blocks to send to the bridge.
     */
    fun getBlocksToBridge () : Array<XyoBridgeQueueItem> {
        return repo.getLowestWeight(sendLimit)
    }

    fun onBlocksBridges (blocks: Array<XyoBridgeQueueItem>) {
        val hashes = ArrayList<XyoBuff>()

        for (block in blocks) {
            hashes.add(block.hash)
        }

        repo.incrementWeights(hashes.toTypedArray())
    }

    /**
     * Get the blocks that have exceeded the removeWeight and are out of the queue
     */
    fun getBlocksToRemove () : Array<XyoBuff> {
        val blocksToBridge = repo.getQueue()
        val toRemoveHashes = ArrayList<XyoBuff>()

        for (block in blocksToBridge) {
            val hash = block.hash
            val weight = block.weight

            if (weight >= removeWeight) {
                toRemoveHashes.add(hash)
            }
        }

        repo.removeQueueItems(toRemoveHashes.toTypedArray())
        return toRemoveHashes.toTypedArray()

    }
}