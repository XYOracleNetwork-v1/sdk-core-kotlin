package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayBase
import java.nio.ByteBuffer

/**
 * An array of signaturePacking for a given party.
 *
 * @major 0x02
 * @minor 0x03
 */
open class XyoSignatureSet(override var array: Array<XyoObject>) : XyoMultiTypeArrayBase() {
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    companion object : XyoArrayProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x03
        override val sizeOfBytesToGetSize: Int? = 2

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).short.toInt()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val unpackedArray = XyoArrayDecoder(byteArray, false, 2)
            val array = unpackedArray.array
            return XyoSignatureSet(array.toTypedArray())
        }
    }
}