package network.xyo.sdkcorekotlin

/**
 * The main result class for the Xyo Network. All functions that can have EXPECTED errors should
 * be wrapped in this object. If the error is null, the value is assumed to be good to go.
 *
 * @constructor creates a new XyoResult with a value, error, or both.
 * @param T the type of the XyoResult contained in value.
 * */
class XyoResult<T> {
    var value: T? = null
    var error: XyoError? = null

    /**
     * Creates a XyoResult with a value and an error.
     *
     * @param value the value for the XyoResult.
     * @param error the error for the XyoResult.
     */
    constructor(value: T?, error: XyoError?) {
        this.value = value
        this.error = error
    }

    /**
     * Creates a XyoResult with a value.
     *
     * @param value the value for the XyoResult.
     */
    constructor(value: T?) {
        this.value = value
        this.error = null
    }

    /**
     * Creates a XyoResult a error.
     *
     * @param error the error for the XyoResult.
     */
    constructor(error: XyoError) {
        this.value = null
        this.error = error
    }

    override fun toString(): String {
        return "XyoResult: V: ${value}, E: ${error?.message ?: error ?: "None"}"
    }
}