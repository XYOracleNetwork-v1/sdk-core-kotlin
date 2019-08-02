package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogFlags
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.repositories.XyoOriginBlockRepository
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * A bound witness options where when the XyoProcedureCatalogFlags.GIVE_ORIGIN_CHAIN flag is set will call the bridge queue
 * to get the latest bridge blocks.
 *
 * @property originBlocks Where the origin blocks are stored to get from the bridge queue. The bridge queue should provide
 * compatible keys.
 * @property bridgeQueue The queue to talk to when the XyoProcedureCatalogFlags.GIVE_ORIGIN_CHAIN flag is set.
 */
open class XyoBridgingOption (private val originBlocks: XyoOriginBlockRepository,
                              private val bridgeQueue: XyoBridgeQueue): XyoBoundWitnessOption {

    private var blocksInTransit : Array<XyoBridgeQueueItem> = arrayOf()

    override val flag: ByteArray = byteArrayOf(XyoProcedureCatalogFlags.TAKE_ORIGIN_CHAIN.toByte())

    override fun onCompleted(boundWitness: XyoBoundWitness?) {
        if (boundWitness != null) {
           bridgeQueue.onBlocksBridged(blocksInTransit)
        }
    }

    override suspend fun getPayload(): XyoBoundWitnessPair? {
        blocksInTransit = bridgeQueue.getBlocksToBridge()
        val blocksToSend = ArrayList<XyoBoundWitness>()
        val blockHashesToSend = ArrayList<XyoObjectStructure>()


        for (block in blocksInTransit) {
            val boundWitness = originBlocks.getOriginBlockByBlockHash(block.hash.bytesCopy).await()

            if (boundWitness != null) {
                blocksToSend.add(boundWitness)
                blockHashesToSend.add(block.hash)
            }
        }

        if (blockHashesToSend.isNotEmpty()) {
            val hashSet = XyoIterableStructure.createTypedIterableObject(XyoSchemas.BRIDGE_HASH_SET, blockHashesToSend.toTypedArray())
            val blockSet = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.BRIDGE_BLOCK_SET, blocksToSend.toTypedArray())

            return XyoBoundWitnessPair(hashSet, blockSet)
        }


        return null
    }
}