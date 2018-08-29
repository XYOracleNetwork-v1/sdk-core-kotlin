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

    private val encodedSize: XyoResult<ByteArray>
        get() {
            if (sizeIdentifierSize.error == null && sizeIdentifierSize.value != null) {
                when (sizeIdentifierSize.value) {
                    1 -> return XyoResult(ByteBuffer.allocate(1).put((totalSize + 1).toByte()).array())
                    2 -> return XyoResult(ByteBuffer.allocate(2).putShort((totalSize + 2).toShort()).array())
                    4 -> return XyoResult(ByteBuffer.allocate(4).putInt(totalSize + 4).array())
                }
            }
            return XyoResult(byteArrayOf())
        }

    private fun makeTyped () : XyoResult<ByteArray> {
        if (encodedSize.error == null && encodedSize.value != null) {
            val buffer = ByteBuffer.allocate(totalSize + encodedSize.value!!.size + 2)
            if (id.error == null && id.value != null) {
                    if (data.error == null && data.value != null) {
                        buffer.put(id.value)
                        buffer.put(encodedSize.value)
                        buffer.put(data.value)
                        return XyoResult(buffer.array())
                    }
                    return XyoResult(XyoError("1"))
                }
                return XyoResult(XyoError("2"))
        } else {
            return encodedSize
        }
    }

    private fun makeUntyped () : XyoResult<ByteArray> {
        if (encodedSize.error == null && encodedSize.value != null) {
            val buffer = ByteBuffer.allocate(totalSize + encodedSize.value!!.size)
            if (data.error == null && data.value != null) {
                buffer.put(encodedSize.value)
                buffer.put(data.value)
            }
            return XyoResult(buffer.array())
        }
        return XyoResult(XyoError(""))
    }
}