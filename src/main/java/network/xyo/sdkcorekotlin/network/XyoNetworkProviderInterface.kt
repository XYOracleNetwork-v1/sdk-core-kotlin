package network.xyo.sdkcorekotlin.network

import network.xyo.sdkcorekotlin.XyoResult

interface XyoNetworkProviderInterface {
    suspend fun find (canDo: XyoNetworkCanDoInterface) : XyoResult<XyoNetworkPipe>
}