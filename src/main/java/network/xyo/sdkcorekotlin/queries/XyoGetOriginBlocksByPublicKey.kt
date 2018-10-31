package network.xyo.sdkcorekotlin.queries

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoGenericItem
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.origin.XyoIndexableOriginBlockRepository
import network.xyo.sdkcorekotlin.origin.XyoOriginRoot
import network.xyo.sdkcorekotlin.origin.XyoOriginVerify

interface XyoGetOriginBlocksByPublicKey : XyoIndexableOriginBlockRepository.Companion.XyoOriginBlockIndexerInterface {
    /**
     * Gets a group of origin blocks that belong to a given party by public key.
     *
     * @param key The public key to search by
     * @return A deferred array of origin blocks found
     */
    fun getOriginChainByPublicKey (key: ByteArray) : Deferred<XyoOriginRoot?>
}