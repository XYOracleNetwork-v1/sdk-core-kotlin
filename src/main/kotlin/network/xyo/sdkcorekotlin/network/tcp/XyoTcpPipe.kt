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

    override suspend fun close() : Boolean {
        try {
            withContext(Dispatchers.IO) {
                socket.shutdownInput()
                socket.shutdownOutput()
                socket.close()
                XyoLog.logDebug("Closing Socket", TAG)
            }
        } catch (exception: IOException) {
            XyoLog.logDebug("Unknown IO While Closing Socket: $exception", TAG)
            return false
        }
        return true
    }

    override fun getNetworkHeuristics(): Array<XyoObjectStructure> {
        return arrayOf()
    }

    override suspend fun send(data: ByteArray, waitForResponse: Boolean): ByteArray? {
        try {
            XyoLog.logDebug("Send Request", TAG)
            return withTimeout(NO_RESPONSE_TIMEOUT.toLong()) {
                return@withTimeout GlobalScope.async(Dispatchers.IO) {
                    return@async send(waitForResponse, data).await()
                }.await()
            }

        } catch (exception: TimeoutCancellationException) {
            XyoLog.logError("Timeout Network Error $exception", TAG, null)
            withContext(Dispatchers.IO) {
                socket.close()
            }
            return null
        }
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

    private fun send(waitForResponse: Boolean, data: ByteArray) = GlobalScope.async(Dispatchers.IO) {
        try {
            val buffer = ByteBuffer.allocate(4 + data.size)
            buffer.putInt(data.size + 4)
            buffer.put(data)

            XyoLog.logDebug("Sending Through TCP ${buffer.array().size}: ${buffer.array().toHexString()}", TAG)

            val outStream = DataOutputStream(socket.getOutputStream())
            outStream.write(buffer.array())

            if (waitForResponse) {
                return@async waitForResponse()
            }

            return@async null

        } catch (exception: IOException) {
            XyoLog.logDebug("Unknown Network Error $exception", TAG)
            return@async null
        }
    }

    companion object {
        const val MAX_READ_SIZE_K_BYTES = 200
        const val NO_RESPONSE_TIMEOUT = 3_000
        const val TAG = "TCP"
    }
}

