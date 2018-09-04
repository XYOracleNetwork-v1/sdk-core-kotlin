package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult

interface XyoNetworkProviderInterface {
    fun getNetworkId() : XyoResult<Int>
    fun getPeers() : XyoResult<Array<XyoNetworkPeer>>
    fun connect(peer : XyoNetworkPeer) : Deferred<XyoResult<XyoNetworkConnection>>
    fun addReceiver(key: String, listenerInterface : XyoNetworkReceiverListenerInterface) : Deferred<XyoError?>
    fun removeReceiver(key: String) : Deferred<XyoError?>
}