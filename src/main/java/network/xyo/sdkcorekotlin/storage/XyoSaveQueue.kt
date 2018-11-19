package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class XyoSaveQueue(private val storageProvider: XyoStorageProviderInterface) {
    fun saveKeys(keys: ByteArray) = GlobalScope.async {
        storageProvider.write(KEYS_KEY.toByteArray(), keys).await()
    }

    fun getKeys() = GlobalScope.async {
        return@async storageProvider.read(KEYS_KEY.toByteArray()).await()
    }

    fun saveWeights(weights: ByteArray) = GlobalScope.async {
        return@async storageProvider.write(WEIGHT_KEY.toByteArray(), weights).await()
    }

    fun getWeights() = GlobalScope.async {
        return@async storageProvider.read(WEIGHT_KEY.toByteArray()).await()
    }

    companion object {
        const val KEYS_KEY = "queueValue"
        const val WEIGHT_KEY = "weightValue"
    }
}