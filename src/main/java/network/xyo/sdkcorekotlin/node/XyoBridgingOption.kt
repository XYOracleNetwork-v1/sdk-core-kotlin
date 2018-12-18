package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import java.lang.ref.WeakReference

/**
 * A bound witness options where when the XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN flag is set will call the bridge queue
 * to get the latest bridge blocks.
 *
 * @param originBlocks Where the origin blocks are stored to get from the bridge queue. The bridge queue should provide
 * compatible keys.
 *
 * @param bridgeQueue The queue to talk to when the XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN flag is set.
 *
 */
open class XyoBridgingOption (private val originBlocks: XyoStorageProviderInterface, private val bridgeQueue: XyoBridgeQueue): XyoBoundWitnessOption() {
    private var hashOfOriginBlocks : XyoBuff? = null
    private var currentBridgingOption : XyoBridgeQueue.XyoBridgeJob? = null
    private var originBlocksToSend : WeakReference<XyoBuff?> = WeakReference(null)

    override val flag: Int = XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN

    override suspend fun getSignedPayload(): XyoBuff? {
        updateOriginChain().await()
        return hashOfOriginBlocks
    }

    override suspend fun getUnsignedPayload(): XyoBuff? {
        return originBlocksToSend.get()
    }

    override fun onCompleted(boundWitness: XyoBoundWitness?) {
        super.onCompleted(boundWitness)

        if (boundWitness != null) {
            currentBridgingOption?.onSucceed()
        }
    }

    private fun updateOriginChain() = GlobalScope.async {
        val job = bridgeQueue.getBlocksToBridge()
        currentBridgingOption = job
        val blockHashes = Array(job.blocks.size) { i ->
            job.blocks[i].boundWitnessHash
        }

        val blocks = ArrayList<XyoBuff>()
        hashOfOriginBlocks = XyoIterableObject.createTypedIterableObject(XyoSchemas.BRIDGE_HASH_SET, blockHashes)

        if (hashOfOriginBlocks != null) {
            for (hash in blockHashes) {
                val blockEncoded = originBlocks.read(hash.bytesCopy).await()
                if (blockEncoded != null) {
                    blocks.add(XyoBoundWitness.getInstance(blockEncoded))
                }
            }
        }

        originBlocksToSend = WeakReference(XyoIterableObject.createUntypedIterableObject(XyoSchemas.BRIDGE_BLOCK_SET, blocks.toTypedArray()))
    }
}