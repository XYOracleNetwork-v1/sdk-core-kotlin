package network.xyo.sdkcorekotlin.network

import network.xyo.sdkcorekotlin.XyoResult

abstract class XyoNetworkPeer {
    abstract fun getType() : XyoResult<ByteArray>
    abstract fun getTemporaryPeerId() : XyoResult<Int>
}