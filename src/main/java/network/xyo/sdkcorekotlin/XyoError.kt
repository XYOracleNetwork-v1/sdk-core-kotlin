package network.xyo.sdkcorekotlin

class XyoError : Error {
    var tag : String? = null
    var errorMessage : String? = null
    var errorCode : XyoErrors? = null

    override val message: String?
        get() = "T: $tag, M: $errorMessage C: $errorCode"

    constructor(errorCode : XyoErrors) {
        this.errorCode = errorCode
    }

    constructor(tag : String, message : String, errorCode : XyoErrors) {
        this.tag = tag
        this.errorCode = errorCode
        this.errorMessage = message
    }

    constructor(tag : String, message: String) {
        this.tag = tag
        this.errorMessage = message
    }
}