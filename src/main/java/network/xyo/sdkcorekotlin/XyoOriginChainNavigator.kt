package network.xyo.sdkcorekotlin

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderPriority

class XyoOriginChainNavigator (private val storageProviderProvider : XyoStorageProviderInterface,
                               private val hashingObject : XyoHash.XyoHashProvider) {

    fun removeOriginBlock (originBlockHash : ByteArray) = async {
        return@async storageProviderProvider.delete(originBlockHash).await()
    }

    fun containsOriginBlock (originBlockHash: ByteArray) = async {
        return@async storageProviderProvider.containsKey(originBlockHash).await()
    }

    fun getAllOriginBlockHashes () = async {
        return@async storageProviderProvider.getAllKeys().await()
    }

    fun addBoundWitness (originBlock : XyoBoundWitness) = async {
        val blockData = originBlock.untyped
        if (blockData.error != null) return@async blockData.error
        val blockDataValue = blockData.value ?: return@async XyoError("Block Data is null!")

        val blockHash = originBlock.getHash(hashingObject).await()
        if (blockHash.error != null) return@async blockHash.error
        val blockHashValue = blockHash.value?.typed?.value ?: return@async XyoError("Block Hash is null!")

        val previousHashes = XyoOriginBlock(originBlock).findPreviousBlocks().await()
        val previousHashesValue = previousHashes.value ?: return@async XyoError("Cant find hash!")

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

    fun getOriginBlockByPreviousHash (originBlockHash: ByteArray) = async {
        val previousHashMerger = XyoByteArraySetter(2)
        previousHashMerger.add(byteArrayOf(0xff.toByte()), 0)
        previousHashMerger.add(originBlockHash, 1)

        val blockHash = storageProviderProvider.read(previousHashMerger.merge(), 60_000).await()
        if (blockHash.error != null) return@async XyoResult<XyoOriginBlock>(XyoError(""))
        val blockHashValue = blockHash.value ?: return@async XyoResult<XyoOriginBlock>(XyoError(""))

        return@async getOriginBlockByBlockHash(blockHashValue).await()
    }

    fun getOriginBlockByBlockHash (originBlockHash: ByteArray) = async {
        val packedOriginBlock = storageProviderProvider.read(originBlockHash, 60_000).await()
        if (packedOriginBlock.error != null) return@async XyoResult<XyoOriginBlock>(XyoError(""))
        val packedOriginBlockValue = packedOriginBlock.value ?: return@async XyoResult<XyoOriginBlock>(XyoError(""))

        val unpackedOriginBlock = XyoBoundWitness.createFromPacked(packedOriginBlockValue)
        if (packedOriginBlock.error != null) return@async XyoResult<XyoOriginBlock>(XyoError(""))
        val unpackedOriginBlockValue = unpackedOriginBlock.value as? XyoBoundWitness ?: return@async XyoResult<XyoOriginBlock>(XyoError(""))

        return@async XyoResult(XyoOriginBlock(unpackedOriginBlockValue))
    }

    inner class XyoOriginBlock(val boundWitness: XyoBoundWitness) {
        fun findPreviousBlocks() = async {
            val previousHashes = ArrayList<ByteArray?>()
            for (payload in boundWitness.payloads) {
                val signedPayload = payload.signedPayloadMapping.value
                val signedPayloadValue = signedPayload ?: return@async XyoResult<ArrayList<ByteArray?>>(XyoError("Mapping is null!"))

                previousHashes.add(signedPayloadValue[XyoPreviousHash.id.contentHashCode()]?.untyped?.value)
            }
            return@async XyoResult(previousHashes)
        }

        fun getHash() = async {
            return@async boundWitness.getHash(hashingObject).await()
        }
    }
}