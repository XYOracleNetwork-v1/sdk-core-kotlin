package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.lang.Exception

class XyoSaveState (private val storageProvider: XyoStorageProviderInterface) {
    fun saveIndex (index : XyoBuff) : Deferred<Exception?> {
        return writeFromKey(INDEX, index.valueCopy)
    }

    fun saveSigners (privateKeys: Array<XyoBuff>) : Deferred<Exception?> {
        return writeFromKey(
                SIGNERS_KEY,
                XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, privateKeys).bytesCopy
        )
    }

    fun savePreviousHash (hash : XyoBuff) : Deferred<Exception?>{
        return writeFromKey(PREV_HASH_KEY, hash.bytesCopy)
    }

    fun getIndex () : Deferred<XyoBuff?> = GlobalScope.async {
        return@async XyoBuff.wrap(readFromKey(INDEX).await() ?: return@async null)
    }

    fun getSigners () : Deferred<Iterator<XyoBuff>?> = GlobalScope.async {
        val encodedSigners = readFromKey(PREV_HASH_KEY).await() ?: return@async null
        return@async object : XyoIterableObject() {
            override val allowedOffset: Int = 0
            override var item: ByteArray = encodedSigners
        }.iterator
    }

    fun getPreviousHash () : Deferred<XyoBuff?> = GlobalScope.async {
        return@async XyoBuff.wrap(readFromKey(PREV_HASH_KEY).await() ?: return@async null)
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