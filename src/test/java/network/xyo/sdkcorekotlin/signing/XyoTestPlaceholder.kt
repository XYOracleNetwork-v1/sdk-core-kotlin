package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

class XyoTestPlaceholder : XyoObject() {
    override val objectInBytes: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(0x00))

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult(0)

    companion object : XyoObjectProvider() {
        override val major: Byte
            get() = 0x09

        override val minor: Byte
            get() = 0x09

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(0)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
           return XyoResult(XyoTestPlaceholder())
        }

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(1)
        }

    }
}