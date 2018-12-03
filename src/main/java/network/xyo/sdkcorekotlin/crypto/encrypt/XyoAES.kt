package network.xyo.sdkcorekotlin.crypto.encrypt

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object XyoAES : XyoEncrypter {
    private val cipher  = Cipher.getInstance("AES/CFB/NoPadding")
    override val algorithmName: String = "AES"
    override val iVSize: Int = 16

    override fun encrypt(password: ByteArray, value: ByteArray, iV: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(hashPassword(password), "AES"), IvParameterSpec(iV))
        return cipher.doFinal(value)
    }

    override fun decrypt(password: ByteArray, encryptedValue: ByteArray, iV: ByteArray): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(hashPassword(password), "AES"), IvParameterSpec(iV))
        return cipher.doFinal(encryptedValue)
    }

    private fun hashPassword (password: ByteArray) : ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(password)
    }
}