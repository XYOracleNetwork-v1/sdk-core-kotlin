package network.xyo.sdkcorekotlin.network

import java.nio.ByteBuffer

class XyoAdvertisePacket (val bytes : ByteArray) {
    fun getChoice () : ByteArray {
        val sizeOfChoice = ByteBuffer.wrap(bytes)[0].toUInt().toInt()
        return bytes.copyOfRange(1, sizeOfChoice - 1)
    }
}