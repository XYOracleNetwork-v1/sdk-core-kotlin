package network.xyo.sdkcorekotlin.network

/**
 * An abstraction for a network peer when talking through a pipe.
 */
abstract class XyoNetworkPeer {
    /**
     * Gets the current role the peer is preforming.
     *
     * @return The current role the peer is preforming .
     */
    abstract fun getRole() : ByteArray

    /**
     * Gets a ID for the peer so it can be remembered.
     *
     * @return The peer ID.
     */
    abstract fun getTemporaryPeerId() : Int
}