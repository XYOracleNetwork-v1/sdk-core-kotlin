package network.xyo.sdkcorekotlin.network

import network.xyo.sdkcorekotlin.XyoResult

/**
 * An abstraction for a network peer when talking through a pipe.
 */
abstract class XyoNetworkPeer {
    /**
     * Gets the current role the peer is preforming.
     *
     * @return The current role the peer is preforming wrapped in a XyoResult.
     */
    abstract fun getRole() : XyoResult<ByteArray>

    /**
     * Gets a ID for the peer so it can be remembered.
     *
     * @return The peer ID wrapped in a XyoResult.
     */
    abstract fun getTemporaryPeerId() : XyoResult<Int>
}