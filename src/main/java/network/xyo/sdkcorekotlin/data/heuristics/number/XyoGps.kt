package network.xyo.sdkcorekotlin.data.heuristics.number

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException
import java.nio.ByteBuffer

class XyoGps (val lat : Double, val lon : Double) : XyoObject() {

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val objectInBytes: ByteArray = encode()

    override val sizeIdentifierSize: Int? = 0

    private fun encode () : ByteArray {
        val buffer = ByteBuffer.allocate(16)
        buffer.putDouble(lat)
        buffer.putDouble(lon)
        return buffer.array()
    }

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x0f
        override val minor: Byte = 0x1f

        override val sizeOfBytesToGetSize: Int? = 0

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            if (byteArray.size != 16)
                throw XyoCorruptDataException("Not sized correctly. Expected 16.")

            val buffer = ByteBuffer.wrap(byteArray)
            val lat = buffer.getDouble(0)
            val lon = buffer.getDouble(8)
            return XyoGps(lat, lon)
        }

        override fun readSize(byteArray: ByteArray): Int {
            return 16
        }
    }
}