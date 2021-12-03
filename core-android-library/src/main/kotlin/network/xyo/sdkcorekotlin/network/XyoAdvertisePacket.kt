package network.xyo.sdkcorekotlin.network

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import java.nio.ByteBuffer

/**
 * Helper object to help parse the advertising stage of the network protocol.   
 */
 
class XyoAdvertisePacket (val bytes : ByteArray) {
    @kotlin.ExperimentalUnsignedTypes
    fun getChoice () : ByteArray {
        val sizeOfChoice = ByteBuffer.wrap(bytes)[0].toUInt().toInt()

        if (sizeOfChoice > bytes.size && bytes.isNotEmpty()) {
            throw XyoObjectException("Invalid choice! XyoAdvertisePacket")
        }

        return bytes.copyOfRange(1, sizeOfChoice + 1)
    }
}