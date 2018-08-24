package network.xyo.sdkcorekotlin

class XyoResult<T> {
    var value: T? = null
    var error: XyoError? = null

    constructor(value: T?, error: XyoError?) {
        this.value = value
        this.error = error
    }

    constructor(value: T?) {
        this.value = value
        this.error = null
    }

    constructor(error: XyoError) {
        this.value = null
        this.error = error
    }

    override fun toString(): String {
        return "XyoResult: V: ${value}, E: ${error?.message ?: error ?: "None"}"
    }
}