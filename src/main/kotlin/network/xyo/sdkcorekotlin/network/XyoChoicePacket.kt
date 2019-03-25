package network.xyo.sdkcorekotlin.network

class XyoChoicePacket (private val bytes: ByteArray) {

    fun getChoice (): ByteArray {
        val size = getSizeOfChoice()
        return bytes.copyOfRange(1, size)
    }

    fun getResponse (): ByteArray {
        val size = getSizeOfChoice()
        return bytes.copyOfRange(size, bytes.size - 1)
    }

    private fun getSizeOfChoice (): Int {
        return bytes[0].toUByte().toInt()
    }
}