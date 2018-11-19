package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter

/**
 * A base class for containing and encoding hashes.
 */
abstract class XyoHash : XyoInterpreter {
    /**
     * The encoded hash.
     */
    abstract val hash : ByteArray


    /**
     * A base class for creating hashes.
     */
    abstract class XyoHashProvider : XyoFromSelf {

        /**
         * Creates a hash given a ByteArray.
         *
         * @param data The data to hash.
         * @return A deferred XyoHash.
         */
        abstract fun createHash (data: ByteArray) : Deferred<XyoHash>
    }
}