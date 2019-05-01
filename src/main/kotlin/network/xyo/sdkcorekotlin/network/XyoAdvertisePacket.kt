package network.xyo.sdkcorekotlin.network

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import java.nio.ByteBuffer

class XyoAdvertisePacket (val bytes : ByteArray) {
    fun getChoice () : ByteArray {
        val sizeOfChoice = ByteBuffer.wrap(bytes)[0].toUInt().toInt()

        if (sizeOfChoice > bytes.size && bytes.isNotEmpty()) {
            throw XyoObjectException("Invalid choice! XyoAdvertisePacket")
        }

        return bytes.copyOfRange(1, sizeOfChoice + 1)
    }
}