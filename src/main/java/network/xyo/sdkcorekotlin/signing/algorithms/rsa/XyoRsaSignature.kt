package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.signing.XyoSignature
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature
import java.nio.ByteBuffer

abstract class XyoRsaSignature (rawSignature: ByteArray) : XyoSignature() {
    private val mSignature = rawSignature

    override val data: ByteArray
        get() = mSignature

    override val sizeIdentifierSize: Int?
        get() = 2

    override val encodedSignature: ByteArray
        get() = mSignature

    abstract class XyoRsaSignatureCreator : XyoObjectCreator () {
        override val defaultSize: Int?
            get() = null

        override val major: Byte
            get() = 0x06

        override val sizeOfSize: Int?
            get() = 2

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val size = ByteBuffer.wrap(byteArray).getShort(2).toInt()

            return object : XyoEcdsaSignature(XyoByteArrayReader(byteArray).read(2, size)) {
                override val id: ByteArray
                    get() = byteArrayOf(byteArray[0], byteArray[1])
            }
        }
    }
}