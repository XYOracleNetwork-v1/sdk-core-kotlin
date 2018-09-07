package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

abstract class XyoHash : XyoObject() {
    abstract val hash : ByteArray

    override val objectInBytes: XyoResult<ByteArray>
        get() = XyoResult(hash)

    abstract class XyoHashProvider : XyoObjectProvider() {
        override val major: Byte = 0x03
        abstract fun createHash (data: ByteArray) : Deferred<XyoResult<XyoHash>>
    }
}