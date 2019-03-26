package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.Deferred

/**
 * A network abstraction to communicate with another peer.
 */
interface  XyoNetworkPipe {

    /**
     * The data that was sent when the pipe was created.
     */
    val initiationData : XyoAdvertisePacket?

    /**
     * Send data to the other end of the peer.
     *
     * @param data The data to send to the other peer.
     * @return A deferred response from the other peer.
     */
    fun send (data : ByteArray, waitForResponse : Boolean) : Deferred<ByteArray?>

    /**
     * Closes the pipe.
     */
    fun close() : Deferred<Any?>
}
