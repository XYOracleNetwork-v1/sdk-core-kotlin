package network.xyo.sdkcorekotlin.persist.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.persist.XyoStorageProvider
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

open class XyoIndexableOriginBlockRepository(storageProviderInterface: XyoHash.XyoHashProvider, hashingProviderInterface: XyoStorageProvider) : XyoStorageOriginBlockRepository(hashingProviderInterface, storageProviderInterface) {
    private val indexers = HashMap<String, XyoOriginBlockIndexerInterface>()

    fun addIndexer(key: String, indexer: XyoOriginBlockIndexerInterface) {
        indexers[key] = indexer
    }

    fun removerIndexer(key: String) {
        indexers.remove(key)
    }

    override fun addBoundWitness(originBlock: XyoBoundWitness) = GlobalScope.async {
        val key = originBlock.getHash(hashingObject).await()

        for ((_, indexer) in indexers) {
            indexer.createIndex(key, originBlock)
        }

        return@async super.addBoundWitness(originBlock).await()
    }

    override fun removeOriginBlock(originBlockHash: XyoBuff) : Deferred<Unit> {
        for ((_, indexer) in indexers) {
            indexer.removeIndex(originBlockHash)
        }

        return super.removeOriginBlock(originBlockHash)
    }

    interface XyoOriginBlockIndexerInterface {
        fun createIndex(blockKey: XyoBuff, block: XyoBoundWitness)
        fun removeIndex(blockKey: XyoBuff)
    }
}