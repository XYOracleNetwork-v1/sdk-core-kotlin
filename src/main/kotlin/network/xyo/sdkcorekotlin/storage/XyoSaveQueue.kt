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

    fun saveStaticts (staticts: Array<XyoBuff>) : Deferred<Unit> = GlobalScope.async {
        val encoded = XyoIterableObject.createUntypedIterableObject(XyoSchemas.PAYMENT_KEY, staticts)

        storageProvider.write(STATICTS_KET.toByteArray(), encoded.bytesCopy).await()

        return@async
    }

    fun getStacticts () : Deferred<Array<XyoBuff>> = GlobalScope.async {
        val encoded = storageProvider.read(STATICTS_KET.toByteArray()).await() ?: return@async arrayOf<XyoBuff>()

        val returnArray = ArrayList<XyoBuff>()
        val array = object : XyoIterableObject() {
            override val allowedOffset: Int = 0
            override var item: ByteArray = encoded
        }.iterator

        for (item in array) {
            returnArray.add(item)
        }
        
        return@async returnArray.toTypedArray()
    }

    companion object {
        const val KEYS_KEY = "queueValue"
        const val WEIGHT_KEY = "weightValue"
        const val STATICTS_KET = "statictsValue"
    }
}