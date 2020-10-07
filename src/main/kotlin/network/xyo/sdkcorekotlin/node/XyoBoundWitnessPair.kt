package network.xyo.sdkcorekotlin.node

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * A base class for bound witness pairs. 
 * @param signedPayload
 * @param unsignedPayload
 */
@ExperimentalStdlibApi
class XyoBoundWitnessPair (val signedPayload: XyoObjectStructure, val unsignedPayload: XyoObjectStructure)