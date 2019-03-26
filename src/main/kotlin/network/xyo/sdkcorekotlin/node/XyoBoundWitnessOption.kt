package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

/**
 * A base class for bound witness options. For example a sentinel would have an option for
 * bridging.
 */
interface XyoBoundWitnessOption {
    /**
     * The flag of the option.
     */
    val flag : ByteArray

    /**
     * Gets the signed data to include in the bound witness.
     *
     * @return The optional signed data.
     */
    suspend fun getPayload() : XyoBoundWitnessPair?

    /**-> XyoObjectStructure
     * This function will be called after the current bound witness has been completed. If the bound witness is null,
     * there was an error creating the bound witness.
     *
     * @param boundWitness The bound witness just completed
     */
    fun onCompleted (boundWitness: XyoBoundWitness?)
}