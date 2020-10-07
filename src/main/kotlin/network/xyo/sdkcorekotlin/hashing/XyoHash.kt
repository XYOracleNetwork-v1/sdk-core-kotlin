package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * A base class for containing and encoding hashes.
 */
@ExperimentalStdlibApi
open class XyoHash : XyoObjectStructure {

    constructor(hash: ByteArray, byteArray: ByteArray, offset: Int = 0) : super(byteArray, offset) {
        this.hash = hash
    }

    /**
     * The encoded hash.
     */
    val hash : ByteArray

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