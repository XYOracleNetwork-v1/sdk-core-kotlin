package network.xyo.sdkcorekotlin.data

class XyoByteArraySetter (size: Int) {
    private val numberOfByteArrays = size
    private var byteArrays = Array(numberOfByteArrays, { defaultValue })
    private val sizes = Array(numberOfByteArrays, { defaultValue.size })
    private var totalSize = defaultValue.size * numberOfByteArrays

    fun add (item: ByteArray, index : Int) {
        byteArrays[index] = item
        totalSize -= sizes[index]
        sizes[index] = item.size
        totalSize += item.size
    }

    fun merge () : ByteArray {
        var position = 0
        val merged =  ByteArray(totalSize)
        for (byteArray in byteArrays) {
            for (byte in byteArray) {
                merged[position] = byte
                position++
            }
        }
        return merged
    }

    companion object {
        val defaultValue = byteArrayOf(0x00)
    }
}