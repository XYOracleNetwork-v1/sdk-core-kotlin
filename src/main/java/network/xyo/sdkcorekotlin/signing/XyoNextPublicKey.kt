package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash

class XyoNextPublicKey (private val publicKey: XyoObject): XyoObject() {
    override val data: XyoResult<ByteArray>
        get() = publicKey.typed

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult<Int?>(null)

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x07

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(2)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val keyCreated = XyoObjectCreator.create(byteArray)
            if (keyCreated.error != null) return XyoResult(XyoError(""))
            val keyCreatedValue = keyCreated.value ?: return XyoResult(XyoError(""))

            return XyoResult(XyoNextPublicKey(keyCreatedValue))
        }

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            val publicKeyCreator = XyoObjectCreator.getCreator(byteArray[0], byteArray[1])
            if (publicKeyCreator.error != null) return XyoResult(XyoError(""))
            val publicKeyCreatorValue = publicKeyCreator.value ?: return XyoResult(XyoError(""))

            val sizeToRead = publicKeyCreatorValue.sizeOfBytesToGetSize
            if (sizeToRead.error != null) return XyoResult(XyoError(""))
            val sizeToReadValue = sizeToRead.value ?: return XyoResult(XyoError(""))
            return XyoResult(publicKeyCreatorValue.readSize(XyoByteArrayReader(byteArray).read(2, sizeToReadValue)).value!! + 2)
        }
    }
}