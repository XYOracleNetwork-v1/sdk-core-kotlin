package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

/**
 * A base class for containing and encoding hashes.
 */
abstract class XyoHash : XyoBuff() {
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
         */
        abstract fun createHash (data: ByteArray) : Deferred<XyoHash>
    }
}