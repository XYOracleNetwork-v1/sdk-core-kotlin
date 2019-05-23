package network.xyo.sdkcorekotlin.network

import kotlin.experimental.and

/**
 * This object is used when creating bit flags for negations between two parties.
 */
object XyoProcedureCatalogFlags {
    /**
     * Can do a standard bound witness.
     */
    const val BOUND_WITNESS = 1

    /**
     * Can do a standard bound witness where it takes the other parties origin chain.
     */
    const val TAKE_ORIGIN_CHAIN = 2

    /**
     * Can do a standard bound witness where it gives its origin chain.
     */
    const val GIVE_ORIGIN_CHAIN = 4

    fun flip (bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val intrestedIn = bytes.last()

        if (intrestedIn and TAKE_ORIGIN_CHAIN.toByte() != 0.toByte()) {
            return byteArrayOf(GIVE_ORIGIN_CHAIN.toByte())
        }

        if (intrestedIn and GIVE_ORIGIN_CHAIN.toByte() != 0.toByte()) {
            return byteArrayOf(TAKE_ORIGIN_CHAIN.toByte())
        }

        return bytes
    }
}
