package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness

/**
 * THe listener for Xyo Nodes.
 */
interface XyoNodeListener {
    /**
     * This function will be called on every bound witness start.
     */
    fun onBoundWitnessStart()

    /**
     * This function will be called on evey time a bound witness discovered for the first time successfully.
     */
    fun onBoundWitnessDiscovered(boundWitness : XyoBoundWitness)

    /**
     * This function will be called on evey time a bound witness did not end successfully.
     */
    fun onBoundWitnessEndFailure(error : Exception?)

    /**
     * This function will be called every time a bound witness is completed successfully.
     */
    fun onBoundWitnessEndSucess (boundWitness: XyoBoundWitness)
}