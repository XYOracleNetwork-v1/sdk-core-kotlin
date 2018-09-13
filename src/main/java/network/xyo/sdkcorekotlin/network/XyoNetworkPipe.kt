package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.experimental.Deferred

/**
 * A network abstraction to communicate with another peer.
 */
abstract class XyoNetworkPipe {
    /**
     * The peer at the other end of the pipe.
     */
    abstract val peer : XyoNetworkPeer

    /**
     * The data that was sent when the pipe was created.
     */
    abstract val initiationData : ByteArray?

    /**
     * Send data to the other end of the peer.
     *
     * @param data The data to send to the other peer.
     * @return A deferred response from the other peer.
     */
    abstract fun send (data : ByteArray) : Deferred<ByteArray?>

    /**
     * Closes the pipe.
     */
    abstract fun close() : Deferred<Any?>
}
