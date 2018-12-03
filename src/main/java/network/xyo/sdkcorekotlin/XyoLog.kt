package network.xyo.sdkcorekotlin

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object XyoLog {
    private val isInDebug = System.getenv("debug") == "true"

    private const val ANSI_RESET = "\u001B[0m"
    private const val ANSI_BLACK = "\u001B[30m"
    private const val ANSI_RED = "\u001B[31m"
    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_YELLOW = "\u001B[33m"
    private const val ANSI_BLUE = "\u001B[34m"
    private const val ANSI_PURPLE = "\u001B[35m"
    private const val ANSI_CYAN = "\u001B[36m"
    private const val ANSI_WHITE = "\u001B[37m"

    /**
     * Logs a debug message.
     */
    fun logDebug (info: String, tag : String) {
        if (isInDebug) {
            println("$ANSI_GREEN${getTime()}$ANSI_RESET $ANSI_YELLOW$tag$ANSI_RESET $info")
        }
    }

    /**
     * Logs any info.
     */
    fun logInfo(info : String, tag: String) {
        println("$ANSI_GREEN${getTime()}$ANSI_RESET $ANSI_YELLOW$tag$ANSI_RESET $info")
    }

    /**
     * Logs a special message
     */
    fun logSpecial (info : String, tag: String) {
        println("$ANSI_GREEN${getTime()}$ANSI_RESET $ANSI_YELLOW$tag$ANSI_RESET $ANSI_PURPLE$info$ANSI_RESET")
    }

    /**
     * Logs an error.
     */
    fun logError (info: String, tag: String, exception: Exception?) {
        println("$ANSI_GREEN${getTime()}$ANSI_RESET $ANSI_YELLOW$tag$ANSI_RESET $ANSI_RED$info$ANSI_RESET")
        exception?.printStackTrace()
    }

    private fun getTime () : String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        return dateFormat.format(date)
    }

    init {
        XyoLog.logDebug("Debug mode on.", "LOG")
    }
}