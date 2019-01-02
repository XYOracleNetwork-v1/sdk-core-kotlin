package network.xyo.sdkcorekotlin.exceptions

/**
 * This Exception is trowed whenever data is malformed.
 *
 * @property message The message of the exception.
 */
class XyoCorruptDataException(override val message: String) : XyoException()