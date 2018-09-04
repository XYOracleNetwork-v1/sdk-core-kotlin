package network.xyo.sdkcorekotlin.network

interface XyoNetworkReceiverListenerInterface {
    fun onConnection(peerConnection : XyoNetworkConnection, peer : XyoNetworkPeer, requestData : ByteArray)
    fun onNewDeviceSeen()
}