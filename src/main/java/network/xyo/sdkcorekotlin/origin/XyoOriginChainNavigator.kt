package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderPriority


/**
 * This class is used as a bucket to store origin blocks, and find links between them.
 *
 * @param storageProviderProvider The storage provider to use when writing encoded origin blocks
 * to storage.
 * @param hashingObject The hashing provider object to hash origin blocks when storing.
 */
class XyoOriginChainNavigator (private val storageProviderProvider : XyoStorageProviderInterface,
                               private val hashingObject : XyoHash.XyoHashProvider) {

    /**
     * Removes an origin block from the navigator and from storage
     *
     * @param originBlockHash the hash of the origin block to be removed.
     * @return A deferred XyoError, if the error is null, the operation was successful.
     */
    fun removeOriginBlock (originBlockHash : ByteArray) = async {
        val previousHashMerger = XyoByteArraySetter(2)
        previousHashMerger.add(byteArrayOf(0xff.toByte()), 0)
        previousHashMerger.add(originBlockHash, 1)
        storageProviderProvider.delete(originBlockHash).await()
        storageProviderProvider.delete(previousHashMerger.merge()).await()
    }

    /**
     * Checks if an origin blocks exists in storage.
     *
     * @param originBlockHash the hash of the origin block to check.
     * @return A deferred XyoResult<Boolean>
     */
    fun containsOriginBlock (originBlockHash: ByteArray) = async {
        return@async storageProviderProvider.containsKey(originBlockHash).await()
    }

    /**
     * Gets all of the origin blocks in storage.
     *
     * @return A deferred XyoResult<Array<ByteArray>>
     */
    fun getAllOriginBlockHashes () = async {
        return@async storageProviderProvider.getAllKeys().await()
    }


    /**
     * Adds a bound bound witness to the navigator and stores it. If the bound witness is not an
     * origin block, it will return an error.
     *
     * @param originBlock The bound witness to add to the navigator.
     * @return A deferred XyoError, if the error is null, the operation was successful.
     */
    fun addBoundWitness (originBlock : XyoBoundWitness) = async {
        val blockData = originBlock.untyped
        if (blockData.error != null) return@async blockData.error
        val blockDataValue = blockData.value ?: return@async XyoError(
                this.toString(),
                "Block Data is null!"
        )

        val blockHash = originBlock.getHash(hashingObject).await()
        if (blockHash.error != null) return@async blockHash.error
        val blockHashValue = blockHash.value?.typed?.value ?: return@async XyoError(
                this.toString(),
                "Block Hash is null!"
        )

        val previousHashes = XyoOriginBlock(originBlock).findPreviousBlocks().await()
        val previousHashesValue = previousHashes.value ?: return@async XyoError(
                this.toString(),
                "Cant find hash!"
        )

        for (hash in previousHashesValue) {
            if (hash != null) {
                val previousHashMerger = XyoByteArraySetter(2)
                previousHashMerger.add(byteArrayOf(0xff.toByte()), 0)
                previousHashMerger.add(hash, 1)

                val error = storageProviderProvider.write(
                        previousHashMerger.merge(),
                        blockHashValue,
                        XyoStorageProviderPriority.PRIORITY_MED,
                        true,
                        60_000
                ).await()

                if (error != null) {
                    return@async error
                }
            }
        }

        return@async storageProviderProvider.write(
                blockHashValue,
                blockDataValue,
                XyoStorageProviderPriority.PRIORITY_MED,
                true,
                60_000
        ).await()
    }

    /**
     * Gets an origin block by its previous hash field.
     *
     * @param originBlockHash The previous hash of the origin block the function is looking
     * to find.
     * @return a deferred XyoResult<XyoOriginBlock> that is has the previous hash.
     */
    fun getOriginBlockByPreviousHash (originBlockHash: ByteArray) = async {
        val previousHashMerger = XyoByteArraySetter(2)
        previousHashMerger.add(byteArrayOf(0xff.toByte()), 0)
        previousHashMerger.add(originBlockHash, 1)

        val blockHash = storageProviderProvider.read(
                previousHashMerger.merge(),
                60_000).await()

        if (blockHash.error != null) return@async XyoResult<XyoOriginBlock>(
                blockHash.error ?: XyoError(
                        this.toString(),
                        "Unknown Previous Hash Error!"
                )
        )
        val blockHashValue = blockHash.value ?: return@async XyoResult<XyoOriginBlock>(
                XyoError(
                        this.toString(),
                        "Previous Hash is null!"
                )
        )

        return@async getOriginBlockByBlockHash(blockHashValue).await()
    }

    /**
     * Gets an origin block by its hash.
     *
     * @param originBlockHash The hash of the origin block the function is looking to find.
     * @return a deferred XyoResult<XyoOriginBlock> that is has the hash.
     */
    fun getOriginBlockByBlockHash (originBlockHash: ByteArray) = async {
        val packedOriginBlock = storageProviderProvider.read(originBlockHash, 1_000).await()
        if (packedOriginBlock.error != null) return@async XyoResult<XyoOriginBlock>(
                packedOriginBlock.error ?: XyoError(
                        this.toString(),
                        "Unknown Read Error!"
                )
        )

        val packedOriginBlockValue = packedOriginBlock.value ?: return@async XyoResult<XyoOriginBlock>(
                XyoError(
                        this.toString(),
                        "Read Value is null!"
                )
        )

        val unpackedOriginBlock = XyoBoundWitness.createFromPacked(packedOriginBlockValue)
        if (packedOriginBlock.error != null) return@async XyoResult<XyoOriginBlock>(
                packedOriginBlock.error ?: XyoError(
                        this.toString(),
                        "Unknown Origin Block Creation Error!"
                )
        )

        val unpackedOriginBlockValue = unpackedOriginBlock.value as? XyoBoundWitness ?: return@async XyoResult<XyoOriginBlock>(
                XyoError(
                        this.toString(),
                        "Origin Block is null!"
                )
        )

        return@async XyoResult(XyoOriginBlock(unpackedOriginBlockValue))
    }

    /**
     * A class to navigate around a origin chain.
     *
     * @param boundWitness The Bound Witness of the origin origin block.
     */
    inner class XyoOriginBlock(val boundWitness: XyoBoundWitness) {

        /**
         * Finds all of the possible previous blocks where the index is the boundWitness.index - 1.
         *
         * @return a deferred XyoResult<ArrayList<ByteArray?>> where the elements are the hashes of
         * possible previous blocks.
         */
        fun findPreviousBlocks() = async {
            val previousHashes = ArrayList<ByteArray?>()
            for (payload in boundWitness.payloads) {
                val signedPayload = payload.signedPayloadMapping.value
                val signedPayloadValue = signedPayload ?: return@async XyoResult<ArrayList<ByteArray?>>(XyoError(this.toString(), "Mapping is null!"))

                previousHashes.add(signedPayloadValue[XyoPreviousHash.id.contentHashCode()]?.untyped?.value)
            }
            return@async XyoResult(previousHashes)
        }

        /**
         * Gets the hash of the origin block. This can be used to call getOriginBlockByPreviousHash
         * to get the blocks successor.
         *
         * @return a deferred XyoResult<ByteArray> of the hash of the block.
         */
        fun getHash() = async {
            return@async boundWitness.getHash(hashingObject).await()
        }
    }
}