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

    fun remove (itemIndex : Int) {
        byteArrays[itemIndex] = defaultValue
    }

    fun getByteArrays () : Array<ByteArray> {
        return byteArrays
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

    fun clear () {
        byteArrays = Array(numberOfByteArrays, { defaultValue })
        totalSize = defaultValue.size * numberOfByteArrays
    }

    private fun getAvoidTotalSize (avoid : Array<Int>) : Int {
        var size = totalSize
        for (byteArray in avoid){
            size -= byteArrays[byteArray * 2].size
            size -= byteArrays[(byteArray * 2) + 1].size
        }
        return size
    }

    companion object {
        val defaultValue = byteArrayOf(0x00)
    }
}