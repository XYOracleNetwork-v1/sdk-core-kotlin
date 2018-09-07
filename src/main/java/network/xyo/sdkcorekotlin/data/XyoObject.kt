package network.xyo.sdkcorekotlin.data

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import java.nio.ByteBuffer

abstract class XyoObject {
    private var dataCache : ByteArray? = null
    private var isChanged = true
    private val bytes : XyoResult<ByteArray>
        get() {
            if (isChanged || dataCache != null) {
                dataCache = objectInBytes.value
                isChanged = false
                return XyoResult(dataCache)
            }
            return XyoResult(dataCache)
        }

    abstract val objectInBytes : XyoResult<ByteArray>
    abstract val sizeIdentifierSize : XyoResult<Int?>
    abstract val id : XyoResult<ByteArray>

    val typed : XyoResult<ByteArray>
        get() = makeTyped()

    val untyped : XyoResult<ByteArray>
        get() = makeUntyped()

    private val totalSize : Int
        get() = bytes.value?.size ?: 0

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

    protected fun updateObjectCache() {
        isChanged = true
    }

    private fun makeTyped () : XyoResult<ByteArray> {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size + 2)
        if (id.error != null)
            return XyoResult(id.error ?: XyoError(this.toString(), "Unknown id error!"))
        if (bytes.error != null)
            return XyoResult(bytes.error ?: XyoError(this.toString(),"Unknown objectInBytes error!"))

        buffer.put(id.value)
        buffer.put(encodedSize)
        buffer.put(bytes.value)
        return XyoResult(buffer.array())
    }

    private fun makeUntyped () : XyoResult<ByteArray> {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size)

        if (bytes.error != null)
            return XyoResult(bytes.error ?: XyoError(this.toString(), "Unknown objectInBytes error!"))

        buffer.put(encodedSize)
        buffer.put(bytes.value)
        return XyoResult(buffer.array())
    }
}