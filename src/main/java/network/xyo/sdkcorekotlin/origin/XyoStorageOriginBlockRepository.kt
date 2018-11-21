package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectIterator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator


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
        removeIndex(originBlockHash).await()
        return@async storageProvider.delete(originBlockHash).await()
    }

    override fun containsOriginBlock(originBlockHash: ByteArray) = GlobalScope.async {
        return@async storageProvider.containsKey(originBlockHash).await()
    }

    override fun getAllOriginBlockHashes() = GlobalScope.async {
        val encodedIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        if (encodedIndex != null) {
            return@async XyoObjectIterator(encodedIndex)
        }

        return@async null
    }

    override fun addBoundWitness(originBlock: XyoBoundWitness) = GlobalScope.async {
        val blockData = originBlock.self
        val blockHash = originBlock.getHash(hashingObject).await()
        updateIndex(blockHash).await()
        return@async storageProvider.write(blockHash.self, blockData).await()
    }

    override fun getOriginBlockByBlockHash(originBlockHash: ByteArray) = GlobalScope.async {
        val packedOriginBlock = storageProvider.read(originBlockHash).await()
                ?: return@async null
        return@async XyoBoundWitness.getInstance(packedOriginBlock)
    }

    private fun updateIndex (blockHash : XyoHash) = GlobalScope.async {
        val newIndex = ArrayList<ByteArray>()
        newIndex.add(blockHash.self)
        val currentIndex = getHashIndex().await()

        if (currentIndex != null) {
            for (hash in currentIndex) {
                newIndex.add(hash)
            }
        }

        val newIndexEncoded = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded)
    }

    private fun removeIndex (blockHash: ByteArray) = GlobalScope.async {
        val newIndex = ArrayList<ByteArray>()
        val currentIndex = getHashIndex().await()

        if (currentIndex != null) {
            for (hash in currentIndex) {
                newIndex.add(hash)
            }
        }

        val newIndexEncoded = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded)
    }

    private fun getHashIndex () : Deferred<Iterator<ByteArray>?> = GlobalScope.async{
        val encodedIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        if (encodedIndex != null) {
            return@async XyoObjectIterator(encodedIndex)
        }
        return@async null
    }

    companion object {
        private val BLOCKS_INDEX_KEY = "blocks".toByteArray()
    }
}