package network.xyo.sdkcorekotlin.node

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * Simple object that ties a block hash to the number of times it's been offloaded by the bridge queue.
 */
@ExperimentalStdlibApi
class XyoBridgeQueueItem (var weight: Int, var hash: XyoObjectStructure)