package network.xyo.sdkcorekotlin.data

/**
 * A class for merging ByteArrays together.
 *
 * @param size The number of ByteArrays to slam together.
 */
class XyoByteArraySetter (size: Int) {
    private val numberOfByteArrays = size
    private var byteArrays = Array(numberOfByteArrays) { defaultValue }
    private val sizes = Array(numberOfByteArrays) { defaultValue.size }
    private var totalSize = defaultValue.size * numberOfByteArrays

    /**
     * Add a ByteArray to get slammed together.
     *
     * @param item The ByteArray to add.
     * @param index The index to add the item.
     */
    fun add (item: ByteArray, index : Int) {
        byteArrays[index] = item
        totalSize -= sizes[index]
        sizes[index] = item.size
        totalSize += item.size
    }

    /**
     * Merge the ByteArrays together.
     *
     * @return The merged ByteArray.
     */
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
        private val defaultValue = byteArrayOf(0x00)
    }
}