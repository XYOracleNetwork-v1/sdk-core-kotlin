package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness

/**
 * A node listener, added through the .addListener() method from XyoOriginChainCreator 
 * to receive callbacks to when a bound witness starts, occurs, discovered, and/or fails.
 */
abstract class XyoNodeListener {
    /**
     * This function will be called on every bound witness start.
     */
    open fun onBoundWitnessStart() {}

    /**
     * This function will be called every time a bound witness is discovered for the first time successfully.
     */
    open fun onBoundWitnessDiscovered(boundWitness : XyoBoundWitness) {}

    /**
     * This function will be called every time a bound witness did not end successfully.
     */
    open fun onBoundWitnessEndFailure(error : Exception?) {}

    /**
     * This function will be called every time a bound witness is completed successfully.
     */
    open fun onBoundWitnessEndSuccess (boundWitness: XyoBoundWitness) {}
}