package network.xyo.sdkcorekotlin.data

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import java.nio.ByteBuffer

abstract class XyoObject {
    abstract val data : XyoResult<ByteArray>
    abstract val sizeIdentifierSize : XyoResult<Int?>
    abstract val id : XyoResult<ByteArray>

    val typed : XyoResult<ByteArray>
        get() = makeTyped()

    val untyped : XyoResult<ByteArray>
        get() = makeUntyped()

    private val totalSize : Int
        get() = data.value?.size ?: 0

    private val encodedSize: ByteArray
        get() {
            if (sizeIdentifierSize.error == null && sizeIdentifierSize.value != null) {
                when (sizeIdentifierSize.value) {
                    1 -> return ByteBuffer.allocate(1).put((totalSize + 1).toByte()).array()
                    2 -> return ByteBuffer.allocate(2).putShort((totalSize + 2).toShort()).array()
                    4 -> return ByteBuffer.allocate(4).putInt(totalSize + 4).array()
                }
            }
            return byteArrayOf()
        }

    private fun makeTyped () : XyoResult<ByteArray> {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size + 2)
        if (id.error != null)
            return XyoResult(id.error ?: XyoError(this.toString(), "Unknown id error!"))
        if (data.error != null)
            return XyoResult(data.error ?: XyoError(this.toString(),"Unknown data error!"))

        buffer.put(id.value)
        buffer.put(encodedSize)
        buffer.put(data.value)
        return XyoResult(buffer.array())
    }

    private fun makeUntyped () : XyoResult<ByteArray> {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size)

        if (data.error != null)
            return XyoResult(data.error ?: XyoError(this.toString(), "Unknown data error!"))

        buffer.put(encodedSize)
        buffer.put(data.value)
        return XyoResult(buffer.array())
    }
}