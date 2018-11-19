package network.xyo.sdkcorekotlin

interface XyoFromSelf {
    fun getInstance (byteArray: ByteArray) : XyoInterpreter
}