package network.xyo.sdkcorekotlin.network

interface XyoNetworkProcedureCatalogueInterface {
    fun canDo(byteArray: ByteArray) : Boolean
    fun getEncodedCanDo() : ByteArray
    fun sizeToRead() : Int
}