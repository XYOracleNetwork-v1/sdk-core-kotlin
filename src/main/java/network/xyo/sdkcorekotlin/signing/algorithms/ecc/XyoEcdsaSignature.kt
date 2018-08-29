package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.signing.XyoSignature

abstract class XyoEcdsaSignature(rawSignature : ByteArray) : XyoSignature() {
    private val signature : ByteArray = rawSignature

    override val data: ByteArray
        get() = signature

    override val sizeIdentifierSize: Int?
        get() = 1

    override val encodedSignature: ByteArray
        get() = signature

    abstract class XyoEcdsaSignatureCreator : XyoObjectCreator() {
        override val major: Byte
            get() = 0x05

        override val sizeOfBytesToGetSize: Int
            get() = 1

        override fun readSize(byteArray: ByteArray): Int {
            return byteArray[0].toInt()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val size = byteArray[0].toInt()

            return object : XyoEcdsaSignature(XyoByteArrayReader(byteArray).read(1, size - 1)) {
                override val id: ByteArray
                    get() = byteArrayOf(major, minor)
            }
        }
    }
}
