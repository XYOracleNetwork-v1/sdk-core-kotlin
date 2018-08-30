package network.xyo.sdkcorekotlin.storage

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult

interface XyoStorageProviderInterface {
    fun write(key: ByteArray, value: ByteArray, priority: XyoStorageProviderPriority, cache: Boolean, timeout: Int): Deferred<XyoError?>
    fun read(key: ByteArray, timeout: Int): Deferred<XyoResult<ByteArray?>>
    fun getAllKeys(): Deferred<XyoResult<Array<ByteArray>>>
    fun delete(key: ByteArray): Deferred<XyoError?>
    fun containsKey(key: ByteArray): Deferred<XyoResult<Boolean>>
}