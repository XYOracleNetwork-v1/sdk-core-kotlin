package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.Exception

class XyoSaveState (private val storageProvider: XyoStorageProviderInterface) {
    fun saveIndex (index : ByteArray) : Deferred<Exception?> {
        return writeFromKey(INDEX, index)
    }

    fun saveSigners (privateKeys: ByteArray) : Deferred<Exception?> {
        return writeFromKey(SIGNERS_KEY, privateKeys)
    }

    fun savePreviousHash (hash : ByteArray) : Deferred<Exception?>{
        return writeFromKey(PREV_HASH_KEY, hash)
    }

    fun getIndex () : Deferred<ByteArray?> {
        return readFromKey(INDEX)
    }

    fun getSigners () : Deferred<ByteArray?> {
        return readFromKey(SIGNERS_KEY)
    }

    fun getPreviousHash () : Deferred<ByteArray?> {
        return readFromKey(PREV_HASH_KEY)
    }

    private fun readFromKey (key : String) = GlobalScope.async {
        return@async storageProvider.read(key.toByteArray()).await()
    }

    private fun writeFromKey (key: String, value : ByteArray) = GlobalScope.async {
        return@async storageProvider.write(key.toByteArray(), value).await()
    }

    companion object {
        const val PREV_HASH_KEY = "prevHash"
        const val SIGNERS_KEY = "privateKeys"
        const val INDEX = "index"
    }
}