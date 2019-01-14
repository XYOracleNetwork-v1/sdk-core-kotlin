package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject


/**
 * This class is used as a bucket to store origin blocks, and find links between them.
 *
 * @property storageProvider The storage provider to use when writing encoded origin blocks
 * to storage.
 * @property hashingObject The hashing provider object to hash origin blocks when storing.
 */
open class XyoStorageOriginBlockRepository(protected val storageProvider: XyoStorageProviderInterface,
                                           protected val hashingObject: XyoHash.XyoHashProvider) : XyoOriginBlockRepository {

    override fun removeOriginBlock(originBlockHash: XyoBuff) = GlobalScope.async {
        removeIndex(originBlockHash.bytesCopy).await()
        storageProvider.delete(originBlockHash.bytesCopy).await()
    }

    override fun containsOriginBlock(originBlockHash: XyoBuff) = GlobalScope.async {
        return@async storageProvider.containsKey(originBlockHash.bytesCopy).await()
    }

    override fun getAllOriginBlockHashes() : Deferred<Iterator<XyoBuff>?> {
        return readIteratorFromKey(BLOCKS_INDEX_KEY)
    }

    private fun readIteratorFromKey (key : ByteArray) : Deferred<Iterator<XyoBuff>?> = GlobalScope.async {
        val encodedIndex = storageProvider.read(key).await()

        if (encodedIndex != null) {
            return@async object : XyoIterableObject() {
                override val allowedOffset: Int
                    get() = 0

                override var item: ByteArray = encodedIndex
            }.iterator
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
        val newIndex =ArrayList<XyoBuff>()
        val currentIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        newIndex.add(blockHash)

        if (currentIndex != null) {
            for (item in object : XyoIterableObject() {
                override val allowedOffset: Int = 0
                override var item: ByteArray = currentIndex
            }.iterator) {
                newIndex.add(item)
            }
        }

        val newIndexEncoded = XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.bytesCopy)
    }

    private fun removeIndex (blockHash: ByteArray) = GlobalScope.async {
        val newIndex = ArrayList<XyoBuff>()
        val currentIndex = getAllOriginBlockHashes().await()

        if (currentIndex != null) {
            for (hash in currentIndex) {
                if (!hash.valueCopy.contentEquals(blockHash)) {
                    newIndex.add(hash)
                }
            }
        }

        val newIndexEncoded = XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.bytesCopy).await()
    }

    companion object {
        private val BLOCKS_INDEX_KEY = "blocks".toByteArray()
    }
}