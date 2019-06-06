package network.xyo.sdkcorekotlin.persist.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.persist.XyoKeyValueStore
import network.xyo.sdkcorekotlin.repositories.XyoOriginBlockRepository
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure


/**
 * This class is used as a bucket to store origin blocks, and find links between them.
 *
 * @property storageProvider The persist provider to use when writing encoded origin blocks
 * to persist.
 * @property hashingObject The hashing provider object to hash origin blocks when storing.
 */
open class XyoStorageOriginBlockRepository(protected val storageProvider: XyoKeyValueStore,
                                           protected val hashingObject: XyoHash.XyoHashProvider) : XyoOriginBlockRepository {

    override fun removeOriginBlock(originBlockHash: XyoObjectStructure) = GlobalScope.async {
        removeIndex(originBlockHash.bytesCopy).await()
        storageProvider.delete(originBlockHash.bytesCopy).await()
    }

    override fun containsOriginBlock(originBlockHash: XyoObjectStructure) = GlobalScope.async {
        return@async storageProvider.containsKey(originBlockHash.bytesCopy).await()
    }

    override fun getAllOriginBlockHashes() : Deferred<Iterator<XyoObjectStructure>?> {
        return readIteratorFromKey(BLOCKS_INDEX_KEY)
    }

    private fun readIteratorFromKey (key : ByteArray) : Deferred<Iterator<XyoObjectStructure>?> = GlobalScope.async {
        val encodedIndex = storageProvider.read(key).await()

        if (encodedIndex != null) {
            return@async XyoIterableStructure(encodedIndex, 0).iterator
        }
        return@async null
    }

    override fun addBoundWitness(originBlock: XyoBoundWitness) = GlobalScope.async {
        val blockData = originBlock.bytesCopy
        val blockHash = originBlock.getHash(hashingObject).await()
        updateIndex(blockHash).await()
        storageProvider.write(blockHash.bytesCopy, blockData).await()
    }

    override fun getOriginBlockByBlockHash(originBlockHash: ByteArray) = GlobalScope.async {
        val packedOriginBlock = storageProvider.read(originBlockHash).await() ?: return@async null
        return@async XyoBoundWitness.getInstance(packedOriginBlock)
    }

    private fun updateIndex (blockHash : XyoHash) = GlobalScope.async {
        val newIndex =ArrayList<XyoObjectStructure>()
        val currentIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        newIndex.add(blockHash)

        if (currentIndex != null) {
            for (item in XyoIterableStructure(currentIndex, 0).iterator) {
                newIndex.add(item)
            }
        }

        val newIndexEncoded = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.bytesCopy)
    }

    private fun removeIndex (blockHash: ByteArray) = GlobalScope.async {
        val newIndex = ArrayList<XyoObjectStructure>()
        val currentIndex = getAllOriginBlockHashes().await()

        if (currentIndex != null) {
            for (hash in currentIndex) {
                if (!hash.bytesCopy.contentEquals(blockHash)) {
                    newIndex.add(hash)
                }
            }
        }

        val newIndexEncoded = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.bytesCopy).await()
    }

    companion object {
        private val BLOCKS_INDEX_KEY = "blocks".toByteArray()
    }
}