package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoByteArraySetter


class XyoNetworkRouter (private val network : XyoNetworkProviderInterface,
                        private val allowedTypes: ByteArray) : XyoNetworkProviderInterface {

    private val listeners = HashMap<String, XyoNetworkReceiverListenerInterface>()

    override fun addReceiver(key: String, listenerInterface: XyoNetworkReceiverListenerInterface) = async {
        listeners[key] = listenerInterface
        return@async null
    }

    override fun removeReceiver(key: String) = async {
        listeners.remove(key)
        return@async null
    }

    override fun connect(peer: XyoNetworkPeer): Deferred<XyoResult<XyoNetworkConnection>> {
        return network.connect(peer)
    }

    override fun getNetworkId(): XyoResult<Int> {
        return network.getNetworkId()
    }

    override fun getPeers(): XyoResult<Array<XyoNetworkPeer>> {
        return network.getPeers()
    }

    private val receiver = object : XyoNetworkReceiverListenerInterface {
        override fun onConnection(peerConnection: XyoNetworkConnection, peer: XyoNetworkPeer, requestData: ByteArray) {
            async {
                val negotiation = doNegotiation(peerConnection, requestData).await()
                val negotiationValue = negotiation.value ?: return@async
                if (negotiation.error != null) {
                    if (negotiation.value?.size == 1) {
                        for ((_, listener) in listeners) {
                            listener.onConnection(NegotiationConnection(peerConnection, negotiation.value), peer, byteArrayOf())
                        }
                    } else {
                        val value = XyoByteArrayReader(negotiationValue).read(1, negotiationValue.size - 1)

                        for ((_, listener) in listeners) {
                            listener.onConnection(peerConnection, peer, value)
                        }
                    }
                } else {
                    peerConnection.disconnect()
                }
            }
        }

        override fun onNewDeviceSeen() {
            for ((_, listener) in listeners) {
                listener.onNewDeviceSeen()
            }
        }
    }

    private class NegotiationConnection(private val parentConnection : XyoNetworkConnection,
                                        private var typePrefix : ByteArray?) : XyoNetworkConnection () {

        override fun disconnect(): Deferred<XyoError?> {
            return parentConnection.disconnect()
        }

        override fun send(data: ByteArray): Deferred<XyoResult<ByteArray>> {
            if (typePrefix != null) {
                val merger = XyoByteArraySetter(2)
                merger.add(typePrefix!!, 0)
                merger.add(data, 1)
                return parentConnection.send(merger.merge())
            }
            return parentConnection.send(data)
        }
    }

    private fun doNegotiation (connection : XyoNetworkConnection, startingData : ByteArray?) = async {
        if (startingData == null) {
            val response = connection.send(allowedTypes).await()
            val responseValue = response.value ?: return@async XyoResult<ByteArray?>(response.error ?: XyoError(this.toString(), "Networking error!"))

            if (allowedTypes.contains(responseValue[0])) {
                return@async response
            } else {
                return@async XyoResult<ByteArray?>(XyoError(this.toString(), "Type not supported!"))
            }
        } else {
            for (byte in startingData) {
                if (allowedTypes.contains(byte)) {
                    return@async XyoResult<ByteArray?>(byteArrayOf(byte))
                } else {
                    return@async XyoResult<ByteArray?>(XyoError(this.toString(), "Type not supported!"))
                }
            }
        }
        return@async XyoResult<ByteArray?>(XyoError(this.toString(), "Type not supported!"))
    }

    init {
        network.addReceiver("router", receiver)
    }
}