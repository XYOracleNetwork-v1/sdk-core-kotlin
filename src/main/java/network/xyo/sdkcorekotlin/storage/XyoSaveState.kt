package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class XyoSaveState (private val storageProvider: XyoStorageProviderInterface) {
    fun saveIndex (index : ByteArray) = GlobalScope.async {
        storageProvider.write("index".toByteArray(), index).await()
    }

    fun saveSigners (privateKeys: ByteArray) = GlobalScope.async {
        storageProvider.write("privateKeys".toByteArray(), privateKeys).await()
    }

    fun savePreviousHash (hash : ByteArray) = GlobalScope.async {
        storageProvider.write("prevHash".toByteArray(), hash).await()
    }

    fun getIndex () : Deferred<ByteArray?> = GlobalScope.async {
        return@async storageProvider.read("index".toByteArray()).await()
    }

    fun getSigners () : Deferred<ByteArray?> = GlobalScope.async {
        return@async storageProvider.read("privateKeys".toByteArray()).await()
    }

    fun getPreviousHash () : Deferred<ByteArray?> = GlobalScope.async {
        return@async storageProvider.read("prevHash".toByteArray()).await()
    }
}