package network.xyo.sdkcorekotlin.data


class XyoByteArrayReader (private val bytes : ByteArray) {
    fun read (offset : Int, size : Int) : ByteArray {
        return bytes.copyOfRange(offset, offset + size)
    }
}