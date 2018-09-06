package network.xyo.sdkcorekotlin.network

import network.xyo.sdkcorekotlin.XyoResult

interface XyoNetworkProviderInterface {
    suspend fun find (procedureCatalogue: XyoNetworkProcedureCatalogueInterface) : XyoResult<XyoNetworkPipe>
}