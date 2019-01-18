package network.xyo.sdkcorekotlin.persist

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject

class XyoSaveState (private val storageProvider: XyoStorageProviderInterface) {
    fun saveIndex (index : XyoBuff) : Deferred<Unit> {
        return writeFromKey(INDEX_KEY, index.bytesCopy)
    }

    fun saveSigners (privateKeys: Array<XyoBuff>) : Deferred<Unit> {
        return writeFromKey(
                SIGNERS_KEY,
                XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, privateKeys).bytesCopy
        )
    }

    fun savePreviousHash (hash : XyoBuff) : Deferred<Unit> {
        return writeFromKey(PREV_HASH_KEY, hash.bytesCopy)
    }

    fun getIndex () : Deferred<XyoBuff?> {
        return readBuffFromKey(INDEX_KEY)
    }

    fun getPreviousHash () : Deferred<XyoBuff?> {
        return readBuffFromKey(PREV_HASH_KEY)
    }

    fun getSigners () : Deferred<Iterator<XyoBuff>?> = GlobalScope.async {
        val encodedSigners = readFromKey(PREV_HASH_KEY).await() ?: return@async null
        return@async object : XyoIterableObject() {
            override val allowedOffset: Int = 0
            override var item: ByteArray = encodedSigners
        }.iterator
    }


    private fun readFromKey (key : String) = GlobalScope.async {
        return@async storageProvider.read(key.toByteArray()).await()
    }

    private fun writeFromKey (key: String, value : ByteArray) = GlobalScope.async {
        return@async storageProvider.write(key.toByteArray(), value).await()
    }

    private fun readBuffFromKey (key : String) : Deferred<XyoBuff> = GlobalScope.async {
        return@async XyoBuff.wrap(readFromKey(key).await() ?: return@async null)
    }

    companion object {
        const val PREV_HASH_KEY = "prevHash"
        const val SIGNERS_KEY = "privateKeys"
        const val INDEX_KEY = "index"
    }
}