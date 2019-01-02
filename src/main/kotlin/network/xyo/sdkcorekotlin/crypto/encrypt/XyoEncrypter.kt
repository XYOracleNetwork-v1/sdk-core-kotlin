package network.xyo.sdkcorekotlin.crypto.encrypt

interface XyoEncrypter {
    val algorithmName : String
    val iVSize : Int
    fun encrypt (password : ByteArray, value : ByteArray, iV: ByteArray) : ByteArray
    fun decrypt (password: ByteArray, encryptedValue: ByteArray, iV: ByteArray) : ByteArray
}