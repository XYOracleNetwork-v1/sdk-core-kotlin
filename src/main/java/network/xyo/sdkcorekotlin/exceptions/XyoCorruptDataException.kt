package network.xyo.sdkcorekotlin.exceptions

/**
 * This Exception is trowed whenever data is malformed.
 *
 * @param message The message to show the Exception.
 */
class XyoCorruptDataException(override val message: String) : XyoException()