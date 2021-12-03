package network.xyo.sdkobjectmodelkotlin.exceptions

import java.lang.Exception

/**
 * A base exception for all all XyoObject related items (all internal functions).
 *
 * @property message The message of the exception.
 */
open class XyoObjectException (override val message: String?) : Exception()