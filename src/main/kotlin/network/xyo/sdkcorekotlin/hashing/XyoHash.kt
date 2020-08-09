package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.Deferred
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * A base class for containing and encoding hashes.
 */
abstract class XyoHash(byteArray: ByteArray, offset: Int = 0)  : XyoObjectStructure(byteArray, offset) {
    /**
     * The encoded hash.
     */
    abstract val hash : ByteArray

    /**
     * A base class for creating hashes.
     */
    abstract class XyoHashProvider {

        /**
         * Creates a hash given a ByteArray.
         */
        abstract suspend fun createHash (data: ByteArray) : XyoHash
    }
}