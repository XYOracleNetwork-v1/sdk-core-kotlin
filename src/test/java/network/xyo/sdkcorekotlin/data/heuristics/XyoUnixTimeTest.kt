package network.xyo.sdkcorekotlin.data.heuristics

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoGps
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoUnixTime
import org.junit.Assert
import org.junit.Test

class XyoUnixTimeTest : XyoTestBase() {
    private val time = 1337L

    @Test
    fun testTime () {
        val time = XyoUnixTime(time)
        val packedTime = time.untyped
        val unpackedTime = XyoUnixTime.createFromPacked(packedTime) as XyoUnixTime

        Assert.assertEquals(time, unpackedTime)
        assertXyoObject(time, unpackedTime)
    }
}