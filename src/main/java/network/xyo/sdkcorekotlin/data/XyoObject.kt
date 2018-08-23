package network.xyo.sdkcorekotlin.data

import java.nio.ByteBuffer

abstract class XyoObject {
    abstract val data : ByteArray
    abstract val sizeIdentifierSize : Int?
    abstract val id : ByteArray

    val typed : ByteArray
        get() = makeTyped()

    val untyped : ByteArray
        get() = makeUntyped()

    private val totalSize : Int
        get() = data.size

    private val encodedSize: ByteArray
        get() {
            if (sizeIdentifierSize != null) {
                when (sizeIdentifierSize) {
                    1 -> return ByteBuffer.allocate(1).put((totalSize + 1).toByte()).array()
                    2 -> return ByteBuffer.allocate(2).putShort((totalSize + 2).toShort()).array()
                    4 -> return ByteBuffer.allocate(4).putInt(totalSize + 2).array()
                }

            }
            return byteArrayOf()
        }

    private fun makeTyped () : ByteArray {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size + 2)
        buffer.put(id)
        buffer.put(encodedSize)
        buffer.put(data)
        return buffer.array()
    }

    private fun makeUntyped () : ByteArray {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size)
        buffer.put(encodedSize)
        buffer.put(data)
        return buffer.array()
    }
}