package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult

abstract class XyoNetworkConnection {
    abstract fun send (data : ByteArray) : Deferred<XyoResult<ByteArray>>
    abstract fun disconnect() : Deferred<XyoError?>
}
