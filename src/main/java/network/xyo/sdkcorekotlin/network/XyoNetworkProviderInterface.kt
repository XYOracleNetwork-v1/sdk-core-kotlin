package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult

interface XyoNetworkProviderInterface {
    suspend fun find (canDo: XyoNetworkCanDoInterface) : XyoResult<XyoNetworkConnection>
}