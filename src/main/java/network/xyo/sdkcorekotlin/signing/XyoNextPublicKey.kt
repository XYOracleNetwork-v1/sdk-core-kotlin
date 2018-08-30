package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

class XyoNextPublicKey (private val publicKey: XyoObject): XyoObject() {
    override val data: XyoResult<ByteArray> = publicKey.typed
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult<Int?>(null)

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x07
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(2)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val keyCreated = XyoObjectProvider.create(byteArray)
            if (keyCreated.error != null) return XyoResult(XyoError(""))
            val keyCreatedValue = keyCreated.value ?: return XyoResult(XyoError(""))

            return XyoResult(XyoNextPublicKey(keyCreatedValue))
        }

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            val publicKeyCreator = XyoObjectProvider.getCreator(byteArray[0], byteArray[1])
            if (publicKeyCreator.error != null) return XyoResult(XyoError(""))
            val publicKeyCreatorValue = publicKeyCreator.value ?: return XyoResult(XyoError(""))

            val sizeToRead = publicKeyCreatorValue.sizeOfBytesToGetSize
            if (sizeToRead.error != null) return XyoResult(XyoError(""))
            val sizeToReadValue = sizeToRead.value ?: return XyoResult(XyoError(""))
            val publicKeyCreatorSize = publicKeyCreatorValue.readSize(XyoByteArrayReader(byteArray).read(2, sizeToReadValue))
            val publicKeyCreatorSizeValue = publicKeyCreatorSize.value ?: return XyoResult(XyoError(""))
            return XyoResult(publicKeyCreatorSizeValue + 2)
        }
    }
}