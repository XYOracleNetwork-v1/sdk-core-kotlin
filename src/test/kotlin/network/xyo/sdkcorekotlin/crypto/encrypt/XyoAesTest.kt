package network.xyo.sdkcorekotlin.crypto.encrypt

import org.junit.Assert
import org.junit.Test

class XyoAesTest {

    @Test
    fun testAes () {
        val password = byteArrayOf(0x00, 0x01)
        val data = byteArrayOf(0x13 ,0x37)
        val iv = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

        val encrypted = XyoAes.encrypt(password, data, iv)
        val decrypted = XyoAes.decrypt(password, encrypted, iv)

        Assert.assertArrayEquals(data, decrypted)
    }
}