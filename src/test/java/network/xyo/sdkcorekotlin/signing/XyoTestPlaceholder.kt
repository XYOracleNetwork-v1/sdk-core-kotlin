package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

class XyoTestPlaceholder : XyoObject() {
    override val objectInBytes: ByteArray
        get() = byteArrayOf(0x00)

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = 0

    companion object : XyoObjectProvider() {
        override val major: Byte
            get() = 0x09

        override val minor: Byte
            get() = 0x09

        override val sizeOfBytesToGetSize: Int?
            get() = 0

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
           return XyoTestPlaceholder()
        }

        override fun readSize(byteArray: ByteArray): Int {
            return 1
        }

    }
}