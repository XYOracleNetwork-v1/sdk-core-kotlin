package network.xyo.sdkcorekotlin.crypto.encrypt

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object XyoAes : XyoEncrypter {
    private val cipher  = Cipher.getInstance("AES/CFB/NoPadding")
    override val algorithmName: String = "AES"
    override val iVSize: Int = 16

    override fun encrypt(password: ByteArray, value: ByteArray, iV: ByteArray): ByteArray {
        return doAes(Cipher.ENCRYPT_MODE, value, password, iV)
    }

    override fun decrypt(password: ByteArray, encryptedValue: ByteArray, iV: ByteArray): ByteArray {
        return doAes(Cipher.DECRYPT_MODE, encryptedValue, password, iV)
    }

    private fun doAes (mode : Int, value : ByteArray, password: ByteArray, iV: ByteArray) : ByteArray {
        cipher.init(mode, SecretKeySpec(hashPassword(password), "AES"), IvParameterSpec(iV))
        return cipher.doFinal(value)
    }

    private fun hashPassword (password: ByteArray) : ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(password)
    }
}