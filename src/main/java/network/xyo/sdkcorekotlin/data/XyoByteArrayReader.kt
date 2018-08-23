package network.xyo.sdkcorekotlin.data


class XyoByteArrayReader (bytes : ByteArray) {
    private val mBytes = bytes

    fun read (offset : Int, size : Int) : ByteArray {
        var currentPosition = 0
        val readBytes = ByteArray(size)

        for (i in offset until offset + size) {
            readBytes[currentPosition] = mBytes[i]
            currentPosition++
        }

        return readBytes
    }
}