package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash

class XyoSaveState (private val storageProvider: XyoStorageProviderInterface) {
    fun saveIndex (index : XyoIndex) {
        storageProvider.write("index".toByteArray(), index.untyped)
    }

    fun saveSigners (privateKeys: Array<XyoObject>) {
        storageProvider.write("privateKeys".toByteArray(), XyoMultiTypeArrayInt(privateKeys).untyped)
    }

    fun savePreviousHash (hash : XyoPreviousHash) {
        storageProvider.write("prevHash".toByteArray(), hash.untyped)
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

    protected fun ByteArray.toHexString(): String {
        val builder = StringBuilder()
        val it = this.iterator()
        builder.append("0x")
        while (it.hasNext()) {
            builder.append(String.format("%02X", it.next()))
        }

        return builder.toString()
    }
}