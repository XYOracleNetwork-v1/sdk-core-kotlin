package network.xyo.sdkcorekotlin.crypto.encrypt

/**
 * XYOEncrypter interface for encryption and decryption taking into it initialization vector size for regular or AES encryption
 *   
*/

interface XyoEncrypter {
    val algorithmName : String
    val iVSize : Int
    fun encrypt (password : ByteArray, value : ByteArray, iV: ByteArray) : ByteArray
    fun decrypt (password: ByteArray, encryptedValue: ByteArray, iV: ByteArray) : ByteArray
}