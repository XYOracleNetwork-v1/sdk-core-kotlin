package network.xyo.sdkcorekotlin.boundWitness

/**
 * This will be thrown when an error happens during the creation of a bound witness.
 *
 * @param message, The message of the error
 */
class XyoBoundWitnessCreationException (override val message: String?) : Exception()
