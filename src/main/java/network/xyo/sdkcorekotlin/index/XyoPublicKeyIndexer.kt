package network.xyo.sdkcorekotlin.index

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter
import network.xyo.sdkcorekotlin.data.XyoGenericItem
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.origin.XyoOriginBlockRepository
import network.xyo.sdkcorekotlin.origin.XyoOriginRoot
import network.xyo.sdkcorekotlin.origin.XyoOriginVerify
import network.xyo.sdkcorekotlin.queries.XyoGetOriginBlocksByPublicKey
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import java.util.*
import kotlin.collections.ArrayList

class XyoPublicKeyIndexer (private val storageProviderInterface: XyoStorageProviderInterface) : XyoGetOriginBlocksByPublicKey {
    override fun createIndex(blockKey: ByteArray, block: XyoBoundWitness) {
        GlobalScope.async {
            val states = getBlockState(block).await()

            for (state in states) {
                indexState(state, blockKey).await()
            }
        }
    }

    override fun getOriginChainByPublicKey(key: ByteArray): Deferred<XyoOriginRoot?> = GlobalScope.async {
        val index = storageProviderInterface.read(key).await()

        if (index != null) {
            val encodedRoot = storageProviderInterface.read(XyoGenericItem.createFromPacked(index).objectInBytes).await()

            if (encodedRoot != null) {
                return@async XyoOriginRoot.createFromPacked(encodedRoot) as XyoOriginRoot
            }
        }

        return@async null
    }

    override fun removeIndex(blockKey: ByteArray) {

    }

    private fun indexState (state : XyoBlockState, key : ByteArray) =  GlobalScope.async {
        if (state.rootKey == null) {
            createNewIndex(state, key).await()
        } else {
            updateIndex(state, key)
        }
    }

    private fun createNewIndex (state : XyoBlockState, key : ByteArray) = GlobalScope.async {
        val nextPub = state.nextPublicKey

        val root = XyoOriginRoot()
        val rootKey = createOriginRootIndexKey(state.keySet.array[0], key).await()
        root.hashes.add(XyoGenericItem(key))

        for (publicKey in state.keySet.array) {
            root.publicKeys.add(publicKey)
            storageProviderInterface.write(createPublicKeyIndexKey(publicKey), XyoGenericItem(rootKey).untyped).await()
        }

        if (nextPub != null) {
            storageProviderInterface.write(createPublicKeyIndexKey(nextPub.publicKey), XyoGenericItem(rootKey).untyped).await()
        }

        storageProviderInterface.write(rootKey, root.untyped).await()
        return@async

    }

    private fun updateIndex (state: XyoBlockState, key: ByteArray) = GlobalScope.async {
        val nextPub = state.nextPublicKey

        if (state.rootKey != null) {
            val encodedRoot = storageProviderInterface.read(state.rootKey).await()

            if (encodedRoot != null) {
                val root = XyoOriginRoot.createFromPacked(encodedRoot) as XyoOriginRoot
                root.hashes.add(XyoGenericItem(key))

                for (publicKey in state.keySet.array) {
                    if (!root.publicKeys.contains(publicKey)) {
                        root.publicKeys.add(publicKey)
                        storageProviderInterface.write(createPublicKeyIndexKey(publicKey), XyoGenericItem(state.rootKey).untyped).await()
                    }
                }

                if (nextPub != null) {
                    storageProviderInterface.write(createPublicKeyIndexKey(nextPub.publicKey), XyoGenericItem(state.rootKey).untyped).await()
                }

                root.updateObjectCache()
                storageProviderInterface.write(state.rootKey, root.untyped).await()
            }
        }
    }

    private fun createOriginRootIndexKey (publicKey: XyoObject, key : ByteArray) = GlobalScope.async {
        val merger = XyoByteArraySetter(2)
        merger.add(publicKey.typed, 0)
        merger.add(key, 1)
        return@async XyoSha256.createHash(merger.merge()).await().typed
    }

    private fun createPublicKeyIndexKey (publicKey: XyoObject) : ByteArray {
        return publicKey.typed
    }

    private fun createIndexIndexKey (index: XyoIndex) : ByteArray {
        return index.typed
    }

    private fun getBlockState (block : XyoBoundWitness) : Deferred<Array<XyoBlockState>> = GlobalScope.async {
        val states = LinkedList<XyoBlockState>()
        for (i in 0 until XyoBoundWitness.getNumberOfParties(block)!!) {

            var rootKey : ByteArray? = null
            val keySet = block.publicKeys[i]
            val nextPublicKey = block.payloads[i].signedPayloadMapping[XyoNextPublicKey.id.contentHashCode()] as? XyoNextPublicKey
            val index = block.payloads[i].signedPayloadMapping[XyoIndex.id.contentHashCode()] as? XyoIndex
            val previousHash = block.payloads[i].signedPayloadMapping[XyoPreviousHash.id.contentHashCode()] as? XyoPreviousHash


            for (key in keySet.array) {
                val keyIndex = storageProviderInterface.read(createPublicKeyIndexKey(key)).await()

                if (keyIndex != null) {
                    rootKey = XyoGenericItem.createFromPacked(keyIndex).objectInBytes
                }
            }

            if (previousHash != null && index != null) {
                states.add(XyoBlockState(index, previousHash, nextPublicKey, keySet, rootKey))
            }
        }

        return@async states.toTypedArray()
    }

    class XyoBlockState (val index : XyoIndex,
                         val previousHash: XyoPreviousHash,
                         val nextPublicKey: XyoNextPublicKey?,
                         val keySet: XyoKeySet,
                         val rootKey: ByteArray?)

    init {
        XyoOriginRoot.enable()
        XyoGenericItem.enable()
    }
}