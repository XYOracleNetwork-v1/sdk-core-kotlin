package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

/**
 * A base class for containing and encoding hashes.
 */
abstract class XyoHash : XyoObject() {
    /**
     * The encoded hash.
     */
    abstract val hash : ByteArray

    override val objectInBytes: XyoResult<ByteArray>
        get() = XyoResult(hash)

    /**
     * A base class for creating hashes.
     */
    abstract class XyoHashProvider : XyoObjectProvider() {
        override val major: Byte = 0x03

        /**
         * Creates a hash given a ByteArray.
         *
         * @param data The data to hash.
         * @return A deferred XyoResult wrapping a XyoHash.
         */
        abstract fun createHash (data: ByteArray) : Deferred<XyoResult<XyoHash>>
    }
}