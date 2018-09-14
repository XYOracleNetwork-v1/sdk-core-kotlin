package network.xyo.sdkcorekotlin.data

import java.lang.ref.WeakReference
import java.nio.ByteBuffer

/**
 * The base class of all encodable objects in XYO Network
 */
abstract class XyoObject {
    private var dataCache : WeakReference<ByteArray?> = WeakReference(null)
    private var isChanged = true
    private val bytes : ByteArray
        get() {
            val cache = dataCache.get()
            if (isChanged || cache == null) {
                dataCache = WeakReference(objectInBytes)
                isChanged = false
                return dataCache.get() ?: objectInBytes
            }
            return cache
        }

    /**
     * The object in byte format wrapped.
     */
    abstract val objectInBytes : ByteArray

    /**
     * The size of the size that should be created for the object.
     */
    abstract val sizeIdentifierSize : Int?

    /**
     * The id of the object (major and minor).
     */
    abstract val id : ByteArray

    /**
     * The object that has its type encoded
     */
    val typed : ByteArray
        get() = makeTyped()

    /**
     * The object that does not have its type encoded.
     */
    val untyped : ByteArray
        get() = makeUntyped()

    private val totalSize : Int
        get() = bytes.size

    private val encodedSize: ByteArray
        get() {
            when (sizeIdentifierSize) {
                1 -> return ByteBuffer.allocate(1).put((totalSize + 1).toByte()).array()
                2 -> return ByteBuffer.allocate(2).putShort((totalSize + 2).toShort()).array()
                4 -> return ByteBuffer.allocate(4).putInt(totalSize + 4).array()
            }
            return byteArrayOf()
        }

    /**
     * Clears the cache of the encoded object.
     */
    fun updateObjectCache() {
        isChanged = true
    }

    private fun makeTyped () : ByteArray {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size + 2)
        buffer.put(id)
        buffer.put(encodedSize)
        buffer.put(bytes)
        return buffer.array()
    }

    private fun makeUntyped () : ByteArray {
        val buffer = ByteBuffer.allocate(totalSize + encodedSize.size)
        buffer.put(encodedSize)
        buffer.put(bytes)
        return buffer.array()
    }
}