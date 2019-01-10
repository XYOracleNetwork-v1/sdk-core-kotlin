package network.xyo.sdkcorekotlin.network

/**
 * This object is used when creating bit flags for negations between two parties.
 */
object XyoProcedureCatalogue {
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
}
