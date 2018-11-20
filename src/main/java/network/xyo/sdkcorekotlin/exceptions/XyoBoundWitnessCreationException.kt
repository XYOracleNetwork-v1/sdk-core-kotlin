package network.xyo.sdkcorekotlin.exceptions

import network.xyo.sdkcorekotlin.exceptions.XyoException

/**
 * This will be thrown when an error happens during the creation of a bound witness.
 *
 * @param message, The message of the error
 */
class XyoBoundWitnessCreationException (override val message: String?) : XyoException()
