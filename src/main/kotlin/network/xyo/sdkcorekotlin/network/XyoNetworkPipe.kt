package network.xyo.sdkcorekotlin.network

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * Network abstraction that can be used for doing bound witnesses with other devices. 
 * This library comes with two implementations, a memory pipe used for testing and simulations, 
 * and a tcp device with for communicating with tcp/ip devices.
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
    suspend fun send (data : ByteArray, waitForResponse : Boolean) : ByteArray?

    /**
     * Closes the pipe.
     */
    suspend fun close() : Any?

    fun getNetworkHeuristics (): Array<XyoObjectStructure>
}
