package network.xyo.sdkcorekotlin.data.heuristics

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.heuristics.number.XyoGps
import org.junit.Assert
import org.junit.Test

class XyoGpsTest : XyoTestBase() {
    private val lat = 17.232
    private val log = -24.232

    @Test
    fun testGps () {
        val gps = XyoGps(lat, log)
        val packedGps = gps.untyped
        val unpackedGps = XyoGps.createFromPacked(packedGps) as XyoGps

        Assert.assertEquals(lat, unpackedGps.lat, 1.toDouble())
        Assert.assertEquals(log, unpackedGps.lon, 1.toDouble())
        assertXyoObject(gps, unpackedGps)
    }
}