package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.Deferred
import java.nio.ByteBuffer

class XyoNetworkHandler (val pipe: XyoNetworkPipe) {

    fun sendCataloguePacket(catalogue: ByteArray) : Deferred<ByteArray?> {
        val buffer = getSizeEncodedCatalogue(catalogue)
        return pipe.send(buffer, true)
    }

    fun sendChoicePacket(catalogue: ByteArray, response: ByteArray): Deferred<ByteArray?> {
        val buffer = ByteBuffer.allocate(catalogue.size + response.size + 1)
                .put(getSizeEncodedCatalogue(catalogue))
                .put(response)
                .array()
        return pipe.send(buffer, true)
    }

    private fun getSizeEncodedCatalogue (catalogue: ByteArray): ByteArray {
        return ByteBuffer.allocate(catalogue.size + 1)
                .put((catalogue.size).toByte())
                .put(catalogue)
                .array()
    }
}
