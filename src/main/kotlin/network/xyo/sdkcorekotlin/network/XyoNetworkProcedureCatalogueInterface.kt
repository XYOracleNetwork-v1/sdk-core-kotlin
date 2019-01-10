package network.xyo.sdkcorekotlin.network

/**
 * Used for advertising what a device can do and support
 */
interface XyoNetworkProcedureCatalogueInterface {
    /**
     * Given a series of bytes. It will return if the device can support that operation.
     *
     * @param byteArray A series of bytes, that is the other parties catalog.
     * @return If the party can do the operation.
     */
    fun canDo(byteArray: ByteArray) : Boolean

    /**
     * What the device can do.
     *
     * @return What the device can do according to its protocol.
     */
    fun getEncodedCanDo() : ByteArray
}