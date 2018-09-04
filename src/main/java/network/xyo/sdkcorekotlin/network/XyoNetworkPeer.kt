package network.xyo.sdkcorekotlin.network

import network.xyo.sdkcorekotlin.XyoResult

abstract class XyoNetworkPeer {
    abstract fun getTemporaryPeerId() : XyoResult<Int>
    abstract fun getComparableNetworks() : XyoResult<Array<Int>>
}