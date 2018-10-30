package network.xyo.sdkcorekotlin.node

import com.sun.corba.se.impl.encoding.CodeSetConversion

/**
 * A class to manage outgoing origin blocks for bridges and sentinels.
 */
open class XyoBridgeQueue {
    private var blocksToBridge = ArrayList<XyoBridgeQueueItem>()
    private var removed = ArrayList<ByteArray>()

    /**
     * The maximum number of blocks to send at a given time.
     */
    var sendLimit = 10

    /**
     * The point at witch blocks should be removed from the queue.
     */
    var removeWeight = 300

    /**
     * Adds an origin block into the bridge queue.
     *
     * @param blockHash The block to add.
     */
    fun addBlock (blockHash : ByteArray) {
        blocksToBridge.add(XyoBridgeQueueItem(blockHash, 0))
        sortQueue()
    }

    /**
     * Adds an origin block into the bridge queue with a weight.
     *
     * @param blockHash The block to add.
     * @param weight The weight to add the block to the queue with.
     */
    fun addBlock (blockHash : ByteArray, weight : Int) {
        blocksToBridge.add(XyoBridgeQueueItem(blockHash, weight))
        sortQueue()
    }

    /**
     * Will filter to queue for a given weight/mask. For example purgeQueue(1) will remove all
     * blocks with a weight of 1 or higher.
     *
     * @param mask The weight to remove.
     */
    fun purgeQueue (mask : Int) {
        for (block in blocksToBridge) {
            if (block.weight >= mask) {
                removed.add(block.boundWitnessHash)
            }
        }
    }

    /**
     * Gets the current list of origin blocks to send.
     *
     * @return An array of blocks to send to the bridge.
     */
    fun getBlocksToBridge () : Array<ByteArray> {
        sortQueue ()

        val toRemove = ArrayList<XyoBridgeQueueItem>()
        val toBridge = ArrayList<ByteArray>()

        for (i in 0 until Math.min(sendLimit, blocksToBridge.size)) {
            val block = blocksToBridge[i]

            toBridge.add(block.boundWitnessHash)
            block.weight++


            if (block.weight >= removeWeight) {
                toRemove.add(block)
            }
        }

        for (block in toRemove) {
            blocksToBridge.remove(block)
            removed.add(block.boundWitnessHash)
        }

        return toBridge.toTypedArray()
    }

    private fun sortQueue () {
        blocksToBridge.sort()
    }

    /**
     * Get the blocks that have exceeded the removeWeight and are out of the queue
     */
    fun getToRemove () : Array<ByteArray> {
        val result = removed.toTypedArray()
        removed.clear()
        return result
    }

    fun setQueue (blocks : Array<ByteArray>, weights: Array<Int>) {
        blocksToBridge = ArrayList(Array(blocks.size) { i -> XyoBridgeQueueItem(blocks[i], weights[i]) }.asList())
    }

    fun getAllBlocks () : Array<ByteArray> {
        return Array(blocksToBridge.size) { i -> blocksToBridge[i].boundWitnessHash }
    }

    fun getAllWeights () : Array<Int> {
        return Array(blocksToBridge.size) { i -> blocksToBridge[i].weight }
    }

    companion object {
        private class XyoBridgeQueueItem (val boundWitnessHash: ByteArray, var weight: Int) : Comparable<XyoBridgeQueueItem> {
            override fun compareTo(other: XyoBridgeQueueItem): Int {
                return weight.compareTo(other.weight)
            }
        }
    }
}