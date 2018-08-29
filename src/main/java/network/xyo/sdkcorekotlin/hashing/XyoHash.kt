package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator

abstract class XyoHash : XyoObject() {
    abstract val hash : ByteArray

    override val data: XyoResult<ByteArray>
        get() = XyoResult(hash)

    abstract class XyoHashCreator : XyoObjectCreator() {
        override val major: Byte
            get() = 0x03

        abstract fun createHash (data: ByteArray) : Deferred<XyoResult<XyoHash>>
    }
}