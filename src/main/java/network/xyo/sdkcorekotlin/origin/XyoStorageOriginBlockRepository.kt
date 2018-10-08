package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface


/**
 * This class is used as a bucket to store origin blocks, and find links between them.
 *
 * @param storageProviderProvider The storage provider to use when writing encoded origin blocks
 * to storage.
 * @param hashingObject The hashing provider object to hash origin blocks when storing.
 */
open class XyoStorageOriginBlockRepository(private val storageProviderProvider: XyoStorageProviderInterface,
                                           private val hashingObject: XyoHash.XyoHashProvider) : XyoOriginBlockRepository {


    override fun removeOriginBlock(originBlockHash: ByteArray) = GlobalScope.async {
        return@async storageProviderProvider.delete(originBlockHash).await()
    }

    override fun containsOriginBlock(originBlockHash: ByteArray) = GlobalScope.async {
        return@async storageProviderProvider.containsKey(originBlockHash).await()
    }

    override fun getAllOriginBlockHashes() = GlobalScope.async {
        return@async storageProviderProvider.getAllKeys().await()
    }

    override fun addBoundWitness(originBlock: XyoBoundWitness) = GlobalScope.async {
        val blockData = originBlock.untyped
        val blockHash = originBlock.getHash(hashingObject).await()
        return@async storageProviderProvider.write(blockHash.typed, blockData).await()
    }

    override fun getOriginBlockByBlockHash(originBlockHash: ByteArray) = GlobalScope.async {
        val packedOriginBlock = storageProviderProvider.read(originBlockHash).await()
                ?: return@async null
        return@async XyoBoundWitness.createFromPacked(packedOriginBlock) as XyoBoundWitness
    }
}