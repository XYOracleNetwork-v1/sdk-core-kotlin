package network.xyo.sdkcorekotlin

class XyoError(private val errorMessage : String) : Error() {
    override val message: String?
        get() = errorMessage
}