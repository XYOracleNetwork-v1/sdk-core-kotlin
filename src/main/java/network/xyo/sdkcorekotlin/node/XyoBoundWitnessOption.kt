package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.data.XyoObject

/**
 * A base class for bound witness options. For example a sentinel would have an option for
 * bridging.
 */
abstract class XyoBoundWitnessOption {
    /**
     * The flag of the option.
     */
    abstract val flag : Int

    /**
     * Gets the signed data to include in the bound witness.
     *
     * @return The optional signed data.
     */
    abstract fun getSignedPayload() : XyoObject?

    /**
     * Gets the unsigned payload to include in the bound witness.
     *
     * @return The option unsigned data.
     */
    abstract fun getUnsignedPayload() : XyoObject?
}