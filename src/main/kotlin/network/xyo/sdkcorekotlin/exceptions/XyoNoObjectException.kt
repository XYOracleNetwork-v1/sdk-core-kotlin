package network.xyo.sdkcorekotlin.exceptions

/**
 * This Exception is trowed whenever data contains a major and minor it does not understand.
 *
 * @property message The message of the exception.
 */
class XyoNoObjectException(override val message: String) : Exception()