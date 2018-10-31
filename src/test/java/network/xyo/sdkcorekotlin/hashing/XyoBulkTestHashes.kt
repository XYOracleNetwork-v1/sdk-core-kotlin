package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.hashing.basic.*
import network.xyo.sdkcorekotlin.hashing.bouncy.XyoSha3
import org.junit.Assert.assertArrayEquals
import org.junit.Test


class XyoBulkTestHashes : XyoTestBase() {
    private val calibrationSeed = byteArrayOf(0x01, 0x02, 0x03)

    @Test
    fun bulkTestHashes () {
        runBlocking {
            testHash(XyoSha3, "FD1780A6FC9EE0DAB26CEB4B3941AB03E66CCD970D1DB91612C66DF4515B0A0A".hexStringToByteArray())
            testHash(XyoMd2, "30BD026F5B88B4719B563BDDB68917BE".hexStringToByteArray())
            testHash(XyoMd5, "5289DF737DF57326FCDD22597AFB1FAC".hexStringToByteArray())
            testHash(XyoSha1, "7037807198C22A7D2B0807371D763779A84FDFCF".hexStringToByteArray())
            testHash(XyoSha224, "3917AAAAA61D81DEB93EF1C27EC647F126FB932894B7CAA9DF286193".hexStringToByteArray())
            testHash(XyoSha256, "039058C6F2C0CB492C533B0A4D14EF77CC0F78ABCCCED5287D84A1A2011CFB81".hexStringToByteArray())
            testHash(XyoSha512, "27864CC5219A951A7A6E52B8C8DDDF6981D098DA1658D96258C870B2C88DFBCB51841AEA172A28BAFA6A79731165584677066045C959ED0F9929688D04DEFC29".hexStringToByteArray())
        }
    }

    private suspend fun testHash (hashCreator : XyoHash.XyoHashProvider, expected : ByteArray) {
        val hashResult = hashCreator.createHash(calibrationSeed).await()
        assertArrayEquals(expected, hashResult.hash)
    }
}