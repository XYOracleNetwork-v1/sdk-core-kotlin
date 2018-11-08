package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoGenericItem
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt

class XyoSaveQueue(private val storageProvider: XyoStorageProviderInterface) {
    fun saveKeys(keys: Array<ByteArray>) = GlobalScope.async {
        val encodedKeys = XyoMultiTypeArrayInt(Array(keys.size) { i ->
            XyoGenericItem(keys[i])
        }).untyped
        storageProvider.write(KEYS_KEY.toByteArray(), encodedKeys).await()
    }

    fun getKeys() = GlobalScope.async {
        val encodedKeys = storageProvider.read(KEYS_KEY.toByteArray()).await()

        if (encodedKeys != null) {
            val unpackedKeys = XyoMultiTypeArrayInt.createFromPacked(encodedKeys) as XyoMultiTypeArrayInt
            return@async Array(unpackedKeys.array.size) { i -> unpackedKeys.array[i].objectInBytes }
        }

        return@async null
    }

    fun saveWeights(weights: Array<Int>) = GlobalScope.async {
        val encodedKeys = XyoMultiTypeArrayInt(Array(weights.size) { i ->
            XyoGenericItem(XyoUnsignedHelper.createUnsignedInt(weights[i]))
        }).untyped
        storageProvider.write(WEIGHT_KEY.toByteArray(), encodedKeys).await()
    }

    fun getWeights() = GlobalScope.async {
        val encodedWeights = storageProvider.read(WEIGHT_KEY.toByteArray()).await()

        if (encodedWeights != null) {
            val unpackedWeights = XyoMultiTypeArrayInt.createFromPacked(encodedWeights) as XyoMultiTypeArrayInt
            return@async Array(unpackedWeights.array.size) { i ->
                XyoUnsignedHelper.readUnsignedInt(unpackedWeights.array[i].objectInBytes)
            }
        }

        return@async null
    }

    companion object {
        const val KEYS_KEY = "queueValue"
        const val WEIGHT_KEY = "weightValue"
    }
}