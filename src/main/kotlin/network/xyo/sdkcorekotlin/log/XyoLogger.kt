package network.xyo.sdkcorekotlin.log

import java.lang.Exception

interface XyoLogger {
    fun logDebug (info: String, tag : String)
    fun logInfo(info : String, tag: String)
    fun logSpecial (info : String, tag: String)
    fun logError (info: String, tag: String, exception: Exception?)
}