package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash

class XyoSaveState (private val storageProvider: XyoStorageProviderInterface) {
    fun saveIndex (index : XyoIndex) = GlobalScope.async {
        storageProvider.write("index".toByteArray(), index.untyped).await()
    }

    fun saveSigners (privateKeys: Array<XyoObject>) = GlobalScope.async {
        storageProvider.write("privateKeys".toByteArray(), XyoMultiTypeArrayInt(privateKeys).untyped).await()
    }

    fun savePreviousHash (hash : XyoPreviousHash) = GlobalScope.async {
        storageProvider.write("prevHash".toByteArray(), hash.untyped).await()
    }

    fun getIndex () : Deferred<XyoIndex?> = GlobalScope.async {
        val encodedIndex = storageProvider.read("index".toByteArray()).await()
        if (encodedIndex != null) {
            return@async XyoIndex.createFromPacked(encodedIndex) as? XyoIndex
        }
        return@async null
    }

    fun getSigners () : Deferred<Array<XyoObject>?> = GlobalScope.async {
        val encodedKeys = storageProvider.read("privateKeys".toByteArray()).await()
        if (encodedKeys != null) {
            return@async (XyoMultiTypeArrayInt.createFromPacked(encodedKeys) as? XyoMultiTypeArrayInt)?.array
        }
        return@async null
    }

    fun getPreviousHash () : Deferred<XyoPreviousHash?> = GlobalScope.async {
        val encodedHash = storageProvider.read("prevHash".toByteArray()).await()
        if (encodedHash != null) {
            return@async XyoPreviousHash.createFromPacked(encodedHash) as? XyoPreviousHash
        }
        return@async null
    }
}