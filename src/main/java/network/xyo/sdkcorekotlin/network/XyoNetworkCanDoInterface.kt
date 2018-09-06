package network.xyo.sdkcorekotlin.network

interface XyoNetworkCanDoInterface {
    fun canDo(byteArray: ByteArray) : Boolean
    fun getEncodedCanDo() : ByteArray
    fun sizeToRead() : Int
}