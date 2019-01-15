package network.xyo.sdkcorekotlin.log

import network.xyo.sdkcorekotlin.XyoTestBase
import org.junit.Test
import java.lang.Exception

class XyoLoggerTest : XyoTestBase() {

    private fun testLogInterface (logger : XyoLogger) {
        logger.logDebug("Test 1", "TEST")
        logger.logError("Test 2", "TEST", Exception("Test Exception"))
        logger.logInfo("Test 3", "TEST")
        logger.logSpecial("Test 4", "TEST")
    }

    @Test
    fun testXyoLog() {
        testLogInterface(XyoLog)
    }
}