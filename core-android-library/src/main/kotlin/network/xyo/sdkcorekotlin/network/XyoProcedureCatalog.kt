package network.xyo.sdkcorekotlin.network

/**
 * Used for advertising what a device can do and support
 */
interface XyoProcedureCatalog {
    fun canDo(byteArray: ByteArray) : Boolean

    fun getEncodedCanDo() : ByteArray

    fun choose (byteArray: ByteArray): ByteArray
}