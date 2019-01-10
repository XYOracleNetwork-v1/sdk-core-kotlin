package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import java.nio.ByteBuffer

class XyoSaveQueue(private val storageProvider: XyoStorageProviderInterface) {
    fun saveKeys(keys: Array<XyoBuff>) = GlobalScope.async {
        storageProvider.write(
                KEYS_KEY.toByteArray(),
                XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, keys).bytesCopy
        ).await()
    }

    fun getKeys() : Deferred<Array<XyoBuff>?> = GlobalScope.async {
        val encoded = storageProvider.read(KEYS_KEY.toByteArray()).await()

        if (encoded != null) {
            val it = object : XyoIterableObject() {
                override val allowedOffset: Int
                    get() = 0

                override var item: ByteArray = encoded
            }
            return@async Array(it.count) { i ->
                it[i]
            }
        }

        return@async null
    }

    fun saveWeights(weights: Array<Int>) = GlobalScope.async {
        return@async storageProvider.write(
                WEIGHT_KEY.toByteArray(),
                XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, Array(weights.size) { i ->
                    XyoBuff.newInstance(XyoSchemas.RSSI, ByteBuffer.allocate(4).putInt(weights[i]).array())
                }).bytesCopy
        ).await()
    }

    fun getWeights() : Deferred<Array<Int>?> = GlobalScope.async {
        val encoded = storageProvider.read(WEIGHT_KEY.toByteArray()).await()

        if (encoded != null) {
            val it = object : XyoIterableObject() {
                override val allowedOffset: Int
                    get() = 0

                override var item: ByteArray = encoded
            }

            return@async Array(it.count) { i ->
                ByteBuffer.wrap(it[i].valueCopy).int
            }
        }

        return@async null
    }

    companion object {
        const val KEYS_KEY = "queueValue"
        const val WEIGHT_KEY = "weightValue"
    }
}