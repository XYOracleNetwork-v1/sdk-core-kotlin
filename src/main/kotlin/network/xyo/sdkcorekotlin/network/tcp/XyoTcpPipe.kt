package network.xyo.sdkcorekotlin.network.tcp

import kotlinx.coroutines.*
import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.network.XyoAdvertisePacket
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import network.xyo.sdkobjectmodelkotlin.toHexString
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.ByteBuffer

/**
 * An implementation of a XyoNetworkPipe using TCP sockets.
 *
 * @property socket The current socket.
 * @param initiationData The data that was sent with the first request.
 */
open class XyoTcpPipe(private val socket: Socket,
                      override var initiationData: XyoAdvertisePacket?) : XyoNetworkPipe {

    override suspend fun close(): Any? {
        return GlobalScope.async(Dispatchers.IO) {
            try {
                socket.shutdownInput()
                socket.shutdownOutput()
                socket.close()
                XyoLog.logDebug("Closing Socket", TAG)
            } catch (exception: IOException) {
                XyoLog.logDebug("Unknown IO While Closing Socket: $exception", TAG)
                return@async null
            }
            return@async null
        }.await()
    }

    override fun getNetworkHeuristics(): Array<XyoObjectStructure> {
        return arrayOf()
    }

    override suspend fun send(data: ByteArray, waitForResponse: Boolean): ByteArray? {
        return GlobalScope.async(Dispatchers.IO) {
            return@async try {
                XyoLog.logDebug("Send Request", TAG)
                withTimeout(NO_RESPONSE_TIMEOUT.toLong()) { send(waitForResponse, data) }

            } catch (exception: TimeoutCancellationException) {
                XyoLog.logError("Timeout Network Error $exception", TAG, null)
                socket.close()
                null
            }
        }.await()
    }

    fun waitForResponse (): ByteArray? {
        XyoLog.logDebug("Waiting for response...", TAG)
        val inStream = DataInputStream(socket.getInputStream())
        val size = ByteArray(4)
        inStream.readFully(size, 0, size.size)
        XyoLog.logDebug("Waiting to read size ${ByteBuffer.wrap(size).int - 4} ${size.toHexString()}", TAG)

        if ((ByteBuffer.wrap(size).int - 4) > (MAX_READ_SIZE_K_BYTES * 1024)) {
            return null
        }

        val message = ByteArray(ByteBuffer.wrap(size).int - 4)
        inStream.readFully(message, 0, message.size)

        XyoLog.logDebug("Read fully: ${message.toHexString()}", TAG)
        return message
    }

    private fun send(waitForResponse: Boolean, data: ByteArray): ByteArray? {
        try {
            val buffer = ByteBuffer.allocate(4 + data.size)
            buffer.putInt(data.size + 4)
            buffer.put(data)

            XyoLog.logDebug("Sending Through TCP ${buffer.array().size}: ${buffer.array().toHexString()}", TAG)

            val outStream = DataOutputStream(socket.getOutputStream())
            outStream.write(buffer.array())

            if (waitForResponse) {
                return waitForResponse()
            }

            return null

        } catch (exception: IOException) {
            XyoLog.logDebug("Unknown Network Error $exception", TAG)
            return null
        }
    }

    companion object {
        const val MAX_READ_SIZE_K_BYTES = 200
        const val NO_RESPONSE_TIMEOUT = 3_000
        const val TAG = "TCP"
    }
}

