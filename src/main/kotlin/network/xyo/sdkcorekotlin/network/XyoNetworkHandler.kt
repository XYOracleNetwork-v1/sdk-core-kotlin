package network.xyo.sdkcorekotlin.network

import java.nio.ByteBuffer

/**
 * Helps send network protocol packets over a XyoNetworkPipe.
 * @property pipe the XYO pipe used for protocol packet sending
 */
class XyoNetworkHandler (val pipe: XyoNetworkPipe) {

    suspend fun sendCataloguePacket(catalogue: ByteArray) : ByteArray? {
        val buffer = getSizeEncodedCatalogue(catalogue)
        return pipe.sendAsync(buffer, true)
    }

    suspend fun sendChoicePacket(catalogue: ByteArray, response: ByteArray): ByteArray? {
        val buffer = ByteBuffer.allocate(catalogue.size + response.size + 1)
                .put(getSizeEncodedCatalogue(catalogue))
                .put(response)
                .array()
        return pipe.sendAsync(buffer, true)
    }

    private fun getSizeEncodedCatalogue (catalogue: ByteArray): ByteArray {
        return ByteBuffer.allocate(catalogue.size + 1)
                .put((catalogue.size).toByte())
                .put(catalogue)
                .array()
    }
}
