package network.xyo.sdkcorekotlin.exceptions

/**
 * This will be thrown when an error happens during the creation of a bound witness.
 *
 * @property message The message of the exception.
 */
class XyoBoundWitnessCreationException (override val message: String?) : XyoException()
