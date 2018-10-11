package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface


/**
 * This class is used as a bucket to store origin blocks, and find links between them.
 *
 * @param storageProvider The storage provider to use when writing encoded origin blocks
 * to storage.
 * @param hashingObject The hashing provider object to hash origin blocks when storing.
 */
open class XyoStorageOriginBlockRepository(protected val storageProvider: XyoStorageProviderInterface,
                                           protected val hashingObject: XyoHash.XyoHashProvider) : XyoOriginBlockRepository {


    override fun removeOriginBlock(originBlockHash: ByteArray) = GlobalScope.async {
        return@async storageProvider.delete(originBlockHash).await()
    }

    override fun containsOriginBlock(originBlockHash: ByteArray) = GlobalScope.async {
        return@async storageProvider.containsKey(originBlockHash).await()
    }

    override fun getAllOriginBlockHashes() = GlobalScope.async {
        return@async storageProvider.getAllKeys().await()
    }

    override fun addBoundWitness(originBlock: XyoBoundWitness) = GlobalScope.async {
        val blockData = originBlock.untyped
        val blockHash = originBlock.getHash(hashingObject).await()
        return@async storageProvider.write(blockHash.typed, blockData).await()
    }

    override fun getOriginBlockByBlockHash(originBlockHash: ByteArray) = GlobalScope.async {
        val packedOriginBlock = storageProvider.read(originBlockHash).await()
                ?: return@async null
        return@async XyoBoundWitness.createFromPacked(packedOriginBlock) as XyoBoundWitness
    }
}