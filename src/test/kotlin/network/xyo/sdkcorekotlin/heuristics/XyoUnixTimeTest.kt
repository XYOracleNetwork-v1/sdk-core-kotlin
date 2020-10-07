package network.xyo.sdkcorekotlin.heuristics

import network.xyo.sdkcorekotlin.XyoTestBase
import org.junit.Assert
import org.junit.Test
@ExperimentalStdlibApi
class XyoUnixTimeTest : XyoTestBase() {

    @Test
    fun testUnixTime () {
        val newUnixTime = XyoUnixTime.getter.getHeuristic() ?: throw Exception("Can not be null!")
        val createdUnixTime = XyoUnixTime.getInstance(newUnixTime.bytesCopy)

        Assert.assertArrayEquals(newUnixTime.bytesCopy, createdUnixTime.bytesCopy)
        Assert.assertEquals(createdUnixTime.time, (newUnixTime as XyoUnixTime).time)
    }
}