package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

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
    abstract suspend fun getSignedPayload() : XyoBuff?

    /**
     * Gets the unsigned payload to include in the bound witness.
     *
     * @return The option unsigned data.
     */
    abstract suspend fun getUnsignedPayload() : XyoBuff?

    /**
     * This function will be called after the current bound witness has been completed. If the bound witness is null,
     * there was an error creating the bound witness.
     *
     * @param boundWitness The bound witness just
     */
    open fun onCompleted (boundWitness: XyoBoundWitness?) {

    }
}