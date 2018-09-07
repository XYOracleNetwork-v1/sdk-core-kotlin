package network.xyo.sdkcorekotlin.data

import java.lang.reflect.Array.getShort
import java.nio.ByteBuffer
import javax.swing.UIManager.put
import kotlin.experimental.and

/**
 * Unsigned ByteBuffer wrapper.
 * Modified from: https://stackoverflow.com/a/9883582
 */

object XyoUnsignedByteBuffer {
    fun getUnsignedByte(byteBuffer: ByteBuffer): Short {
        return byteBuffer.get().toShort() and 0xff.toShort()
    }

    fun putUnsignedByte(byteBuffer: ByteBuffer, value: Int) {
        byteBuffer.put((value and 0xff).toByte())
    }

    fun getUnsignedByte(byteBuffer: ByteBuffer, position: Int): Short {
        return byteBuffer.get(position).toShort() and 0xff.toShort()
    }

    fun putUnsignedByte(byteBuffer: ByteBuffer, position: Int, value: Int) {
        byteBuffer.put(position, (value and 0xff).toByte())
    }


    fun getUnsignedShort(byteBuffer: ByteBuffer): Int {
        return byteBuffer.getShort().toInt() and 0xffff
    }

    fun putUnsignedShort(byteBuffer: ByteBuffer, value: Int) {
        byteBuffer.putShort((value and 0xffff).toShort())
    }

    fun getUnsignedShort(byteBuffer: ByteBuffer, position: Int): Int {
        return byteBuffer.getShort(position).toInt() and 0xffff
    }

    fun putUnsignedShort(byteBuffer: ByteBuffer, position: Int, value: Int) {
        byteBuffer.putShort(position, (value and 0xffff).toShort())
    }


    fun getUnsignedInt(byteBuffer: ByteBuffer): Long {
        return byteBuffer.getInt().toLong() and 0xffffffffL
    }

    fun putUnsignedInt(byteBuffer: ByteBuffer, value: Long) {
        byteBuffer.putInt((value and 0xffffffffL).toInt())
    }

    fun getUnsignedInt(byteBuffer: ByteBuffer, position: Int): Long {
        return byteBuffer.getInt(position).toLong() and 0xffffffffL
    }

    fun putUnsignedInt(byteBuffer: ByteBuffer, position: Int, value: Long) {
        byteBuffer.putInt(position, (value and 0xffffffffL).toInt())
    }
}