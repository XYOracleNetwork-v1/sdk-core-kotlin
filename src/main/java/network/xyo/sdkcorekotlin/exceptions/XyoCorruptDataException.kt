package network.xyo.sdkcorekotlin.exceptions

/**
 * This Exception is trowed whenever data is malformed.
 */
class XyoCorruptDataException(override val message: String) : XyoException()