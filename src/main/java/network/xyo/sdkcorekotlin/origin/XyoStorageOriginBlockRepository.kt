package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
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
        removeIndex(XyoObjectProvider.create(originBlockHash)!!).await()
        return@async storageProvider.delete(originBlockHash).await()
    }

    override fun containsOriginBlock(originBlockHash: ByteArray) = GlobalScope.async {
        return@async storageProvider.containsKey(originBlockHash).await()
    }

    override fun getAllOriginBlockHashes() = GlobalScope.async {
        val encodedIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        if (encodedIndex != null) {
            val index = (XyoMultiTypeArrayInt.createFromPacked(encodedIndex) as XyoMultiTypeArrayInt).array

            return@async Array(index.size) { i -> index[i].typed }
        }

        return@async arrayOf<ByteArray>()
    }

    override fun addBoundWitness(originBlock: XyoBoundWitness) = GlobalScope.async {
        val blockData = originBlock.untyped
        val blockHash = originBlock.getHash(hashingObject).await()
        updateIndex(blockHash).await()
        return@async storageProvider.write(blockHash.typed, blockData).await()
    }

    override fun getOriginBlockByBlockHash(originBlockHash: ByteArray) = GlobalScope.async {
        val packedOriginBlock = storageProvider.read(originBlockHash).await()
                ?: return@async null
        return@async XyoBoundWitness.createFromPacked(packedOriginBlock) as XyoBoundWitness
    }

    private fun updateIndex (blockHash : XyoHash) = GlobalScope.async {
        val newIndex = ArrayList<XyoObject>()
        newIndex.add(blockHash)
        val currentIndex = getHashIndex().await()?.array

        if (currentIndex != null) {
            for (hash in currentIndex) {
                newIndex.add(hash)
            }
        }

        val newIndexEncoded = XyoMultiTypeArrayInt(newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.untyped)
    }

    private fun removeIndex (blockHash: XyoObject) = GlobalScope.async {
        val newIndex = ArrayList<XyoObject>()
        val currentIndex = getHashIndex().await()?.array

        if (currentIndex != null) {
            for (hash in currentIndex) {
                if (blockHash != hash) {
                    newIndex.add(hash)
                }
            }
        }

        val newIndexEncoded = XyoMultiTypeArrayInt(newIndex.toTypedArray())
        storageProvider.write(BLOCKS_INDEX_KEY, newIndexEncoded.untyped)
    }

    private fun getHashIndex () : Deferred<XyoMultiTypeArrayInt?> = GlobalScope.async{
        val encodedIndex = storageProvider.read(BLOCKS_INDEX_KEY).await()

        if (encodedIndex != null) {
            return@async XyoMultiTypeArrayInt.createFromPacked(encodedIndex) as XyoMultiTypeArrayInt
        }
        return@async null
    }

    companion object {
        private val BLOCKS_INDEX_KEY = "blocks".toByteArray()
    }
}