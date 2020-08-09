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

    override suspend fun removeOriginBlock(originBlockHash: XyoObjectStructure) {
        removeIndex(originBlockHash.bytesCopy)
        storageProvider.delete(originBlockHash.bytesCopy)
    }

    override suspend fun containsOriginBlock(originBlockHash: XyoObjectStructure): Boolean {
        return storageProvider.containsKey(originBlockHash.bytesCopy)
    }

    override suspend fun getAllOriginBlockHashes() : Iterator<XyoObjectStructure>? {
        return readIteratorFromKey(BLOCKS_INDEX_KEY)
    }

    private suspend fun readIteratorFromKey (key : ByteArray) : Iterator<XyoObjectStructure>? {
        val encodedIndex = storageProvider.read(key)

        if (encodedIndex != null) {
            return XyoIterableStructure(encodedIndex, 0).iterator
        }
        return null
    }

    override suspend fun addBoundWitness(originBlock: XyoBoundWitness) {
        val blockData = originBlock.bytesCopy
        val blockHash = originBlock.getHash(hashingObject)
        updateIndex(blockHash)
        storageProvider.write(blockHash.bytesCopy, blockData)
    }

    override suspend fun getOriginBlockByBlockHash(originBlockHash: ByteArray): XyoBoundWitness? {
        val packedOriginBlock = storageProvider.read(originBlockHash) ?: return null
        return XyoBoundWitness.getInstance(packedOriginBlock)
    }

    private suspend fun updateIndex (blockHash : XyoHash) {
        val newIndex =ArrayList<XyoObjectStructure>()
        val currentIndex = storageProvider.read(BLOCKS_INDEX_KEY)

        newIndex.add(blockHash)

        if (currentIndex != null) {
            for (item in XyoIterableStructure(currentIndex, 0).iterator) {
                newIndex.add(item)
            }
        }

        val newIndexEncoded = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.bytesCopy)
    }

    private suspend fun removeIndex (blockHash: ByteArray) {
        val newIndex = ArrayList<XyoObjectStructure>()
        val currentIndex = getAllOriginBlockHashes()

        if (currentIndex != null) {
            for (hash in currentIndex) {
                if (!hash.bytesCopy.contentEquals(blockHash)) {
                    newIndex.add(hash)
                }
            }
        }

        val newIndexEncoded = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.bytesCopy)
    }

    companion object {
        private val BLOCKS_INDEX_KEY = "blocks".toByteArray()
    }
}