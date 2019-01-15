package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness

/**
 * THe listener for Xyo Nodes.
 */
abstract class XyoNodeListener {
    /**
     * This function will be called on every bound witness start.
     */
    open fun onBoundWitnessStart() {}

    /**
     * This function will be called on evey time a bound witness discovered for the first time successfully.
     */
    open fun onBoundWitnessDiscovered(boundWitness : XyoBoundWitness) {}

    /**
     * This function will be called on evey time a bound witness did not end successfully.
     */
    open fun onBoundWitnessEndFailure(error : Exception?) {}

    /**
     * This function will be called every time a bound witness is completed successfully.
     */
    open fun onBoundWitnessEndSuccess (boundWitness: XyoBoundWitness) {}
}