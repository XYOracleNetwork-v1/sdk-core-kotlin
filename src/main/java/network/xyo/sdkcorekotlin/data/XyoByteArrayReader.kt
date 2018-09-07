package network.xyo.sdkcorekotlin.data

/**
 * A standard class used for reading from a ByteArray.
 */
class XyoByteArrayReader (private val bytes : ByteArray) {
    fun read (offset : Int, size : Int) : ByteArray {
        return bytes.copyOfRange(offset, offset + size)
    }
}