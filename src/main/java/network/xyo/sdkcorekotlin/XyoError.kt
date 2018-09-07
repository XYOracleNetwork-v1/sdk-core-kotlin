package network.xyo.sdkcorekotlin

/**
 * The error object to be used when an error is EXPECTED to be possible. For example,
 * corrupted data.
 */
class XyoError : Error {
    /**
     * The tag of the error. This is useful for finding the origin of the error. e.g function name.
     */
    var tag : String? = null

    /**
     * The error message that may contain other useful information.
     */
    var errorMessage : String? = null

    /**
     * The XyoErros Enum code for the error.
     */
    var errorCode : XyoErrors? = null

    override val message: String?
        get() = "T: $tag, M: $errorMessage C: $errorCode"

    /**
     * Creates an XyoObject with a single error code.
     *
     * @param errorCode The XyoErros error code.
     */
    constructor(errorCode : XyoErrors) {
        this.errorCode = errorCode
    }

    /**
     * Creates an XyoObject with a single error code, tag, and message.
     *
     * @param tag The tag of the error.
     * @param message The error message.
     * @param errorCode The XyoErros error code.
     */
    constructor(tag : String, message : String, errorCode : XyoErrors) {
        this.tag = tag
        this.errorCode = errorCode
        this.errorMessage = message
    }

    /**
     * Creates an XyoObject with a single error code and tag.
     *
     * @param tag The tag of the error.
     * @param errorCode The XyoErros error code.
     */
    constructor(tag : String, message: String) {
        this.tag = tag
        this.errorMessage = message
    }
}