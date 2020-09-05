package network.xyo.sdkcorekotlin.log

import java.lang.Exception

object XyoLog : XyoLogger {
    var logger : XyoLogger = XyoAsciiLogger

    override fun logDebug(info: String, tag: String) {
        logger.logDebug(info, tag)
    }

    override fun logError(info: String, tag: String, exception: Exception?) {
        logger.logError(info, tag, exception)
    }

    override fun logInfo(info: String, tag: String) {
        logger.logInfo(info, tag)
    }

    override fun logSpecial(info: String, tag: String) {
        logger.logSpecial(info, tag)
    }
}