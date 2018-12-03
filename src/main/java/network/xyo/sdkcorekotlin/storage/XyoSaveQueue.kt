package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import java.nio.ByteBuffer

class XyoSaveQueue(private val storageProvider: XyoStorageProviderInterface) {
    fun saveKeys(keys: Array<ByteArray>) = GlobalScope.async {
        storageProvider.write(
                KEYS_KEY.toByteArray(),
                XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, keys)
        ).await()
    }

    fun getKeys() : Deferred<Array<ByteArray>?> = GlobalScope.async {
        val encoded = storageProvider.read(KEYS_KEY.toByteArray()).await()

        if (encoded != null) {
            val it = XyoIterableObject(encoded)
            return@async Array(it.size) { i ->
                it[i]
            }
        }

        return@async null
    }

    fun saveWeights(weights: Array<Int>) = GlobalScope.async {
        return@async storageProvider.write(
                WEIGHT_KEY.toByteArray(),
                XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, Array(weights.size) { i ->
                    XyoObjectCreator.createObject(XyoSchemas.RSSI, ByteBuffer.allocate(4).putInt(weights[i]).array())
                })
        ).await()
    }

    fun getWeights() : Deferred<Array<Int>?> = GlobalScope.async {
        val encoded = storageProvider.read(WEIGHT_KEY.toByteArray()).await()

        if (encoded != null) {
            val it = XyoIterableObject(encoded)
            return@async Array(it.size) { i ->
                ByteBuffer.wrap(XyoObjectCreator.getObjectValue(it[i])).int
            }
        }

        return@async null
    }

    companion object {
        const val KEYS_KEY = "queueValue"
        const val WEIGHT_KEY = "weightValue"
    }
}