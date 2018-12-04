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
 * @param storageProvider The storage provider to use when writing encoded origin blocks
 * to storage.
 * @param hashingObject The hashing provider object to hash origin blocks when storing.
 */
open class XyoStorageOriginBlockRepository(protected val storageProvider: XyoStorageProviderInterface,
                                           protected val hashingObject: XyoHash.XyoHashProvider) : XyoOriginBlockRepository {


    override fun removeOriginBlock(originBlockHash: XyoBuff) = GlobalScope.async {
        removeIndex(originBlockHash.bytesCopy).await()
        return@async storageProvider.delete(originBlockHash.bytesCopy).await()
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
        return@async storageProvider.write(blockHash.bytesCopy, blockData).await()
    }

    override fun getOriginBlockByBlockHash(originBlockHash: ByteArray) = GlobalScope.async {
        val packedOriginBlock = storageProvider.read(originBlockHash).await()
                ?: return@async null
        return@async XyoBoundWitness.getInstance(packedOriginBlock)
    }

    private fun updateIndex (blockHash : XyoHash) = GlobalScope.async {
        val newIndex = arrayOf(blockHash.bytesCopy)
        val currentIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        // throw Exception("Stub!")

//        if (currentIndex != null) {
//            val newIndexEncoded =  XyoObjectSetCreator.addToIterableObject(blockHash.bytesCopy, currentIndex)
//            return@async storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded)
//
//        }
//
//        val newIndexEncoded = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, newIndex)
//        return@async storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded)
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
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.valueCopy)
    }

    companion object {
        private val BLOCKS_INDEX_KEY = "blocks".toByteArray()
    }
}