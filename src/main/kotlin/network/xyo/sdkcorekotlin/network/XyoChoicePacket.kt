package network.xyo.sdkcorekotlin.network

class XyoChoicePacket (private val bytes: ByteArray) {

    fun getChoice (): ByteArray {
        val size = getSizeOfChoice()
        return bytes.copyOfRange(1, size + 1)
    }

    fun getResponse (): ByteArray {
        val size = getSizeOfChoice()
        return bytes.copyOfRange(size + 1, bytes.size)
    }

    private fun getSizeOfChoice (): Int {
        return bytes[0].toUByte().toInt()
    }
}