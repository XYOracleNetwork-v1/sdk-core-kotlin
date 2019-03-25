package network.xyo.sdkcorekotlin.queries

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.persist.XyoStorageException
import network.xyo.sdkcorekotlin.persist.repositories.XyoIndexableOriginBlockRepository

interface XyoGetOriginBlocksByPublicKey : XyoIndexableOriginBlockRepository.XyoOriginBlockIndexerInterface {
    /**
     * Gets a group of origin blocks that belong to a given party by public key.
     *
     * @param key The public key to search by
     * @return A deferred array of origin blocks found
     * @throws XyoStorageException if there is a problem reading
     */
    @Throws(XyoStorageException::class)
    fun getOriginChainByPublicKey (key: ByteArray) : Deferred<ByteArray?>
}