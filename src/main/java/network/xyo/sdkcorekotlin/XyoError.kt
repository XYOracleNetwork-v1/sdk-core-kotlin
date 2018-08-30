package network.xyo.sdkcorekotlin

class XyoError : Error {
    var errorMessage : String? = null
    var errorCode : XyoErrors? = null

    override val message: String?
        get() = "M: $errorMessage C: $errorCode"

    constructor(errorCode : XyoErrors) {
        this.errorCode = errorCode
    }

    constructor(message : String, errorCode : XyoErrors) {
        this.errorCode = errorCode
        this.errorMessage = message
    }

    constructor(message: String) {
        this.errorMessage = message
    }
}