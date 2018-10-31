package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.array.multi.XyoBridgeHashSet
import network.xyo.sdkcorekotlin.data.array.single.XyoBridgeBlockSet
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import java.lang.ref.WeakReference

class XyoBridgingOption (private val originBlocks: XyoStorageProviderInterface, private val bridgeQueue: XyoBridgeQueue): XyoBoundWitnessOption() {
    private var hashOfOriginBlocks : XyoBridgeHashSet? = null
    private var originBlocksToSend : WeakReference<XyoObject?> = WeakReference(null)

    override val flag: Int = XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN

    override suspend fun getSignedPayload(): XyoObject? {
        updateOriginChain().await()
        return hashOfOriginBlocks
    }

    override suspend fun getUnsignedPayload(): XyoObject? {
        return originBlocksToSend.get()
    }

    private fun updateOriginChain() = GlobalScope.async {
        val blockHashes = bridgeQueue.getBlocksToBridge()
        val blocks = ArrayList<XyoObject>()
        hashOfOriginBlocks = XyoBridgeHashSet(XyoObjectProvider.encodedToDecodedArray(blockHashes))


        if (hashOfOriginBlocks != null) {
            for (hash in blockHashes) {
                val blockEncoded = originBlocks.read(hash).await()
                if (blockEncoded != null) {
                    try {
                        blocks.add(XyoBoundWitness.createFromPacked(blockEncoded))
                    } catch (exception : Exception) {
                        originBlocks.delete(hash).await()
                    }
                }
            }
        }

        originBlocksToSend = WeakReference(XyoBridgeBlockSet(blocks.toTypedArray()))
    }
}