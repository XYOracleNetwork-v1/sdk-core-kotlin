package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject

/**
 * A class to manage outgoing origin blocks for bridges and sentinels.
 */
open class XyoBridgeQueue {
    private class XyoBridgeQueueItem (val boundWitness: XyoBoundWitness, var weight: Int) : Comparable<XyoBridgeQueueItem> {
        override fun compareTo(other: XyoBridgeQueueItem): Int {
            return weight - other.weight
        }
    }
    private val blocksToBridge = ArrayList<XyoBridgeQueueItem>()

    /**
     * Adds an origin block into the bridge queue.
     *
     * @param block The block to add.
     */
    fun addBlock (block : XyoBoundWitness) {
        blocksToBridge.add(XyoBridgeQueueItem(block, 0))
        sortQueue()
    }

    /**
     * Adds an origin block into the bridge queue with a weight.
     *
     * @param block The block to add.
     * @param weight The weight to add the block to the queue with.
     */
    fun addBlock (block : XyoBoundWitness, weight : Int) {
        blocksToBridge.add(XyoBridgeQueueItem(block, weight))
        sortQueue()
    }

    /**
     * Gets the current list of origin blocks to send.
     *
     * @return An array of blocks to send to the bridge.
     */
    fun getBlocksToBridge () : Array<XyoObject> {
        val toRemove = ArrayList<XyoBridgeQueueItem>()
        val toBridge = ArrayList<XyoObject>()

        for (i in 0 until Math.min(SENT_LIMIT, blocksToBridge.size)) {
            val block = blocksToBridge[i]
            toBridge.add(block.boundWitness)
            block.weight++

            if (block.weight >= REMOVE_WEIGHT) {
                toRemove.add(block)
            }
        }

        for (block in toRemove) {
            blocksToBridge.remove(block)
        }

        return toBridge.toTypedArray()
    }

    private fun sortQueue () {
        blocksToBridge.sort()
    }

    companion object {
        /**
         * The maximum number of blocks to send at a given time.
         */
        private const val SENT_LIMIT = 2

        /**
         * The point at witch blocks should be removed from the queue.
         */
        private const val REMOVE_WEIGHT = 5
    }
}