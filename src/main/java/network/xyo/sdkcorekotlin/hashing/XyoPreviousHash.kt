package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator

open class XyoPreviousHash(val hash: XyoHash) : XyoObject() {
    override val data: XyoResult<ByteArray>
        get() = hash.typed

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult<Int?>(null)

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x06

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            val hashCreator = XyoObjectCreator.getCreator(byteArray[0], byteArray[1])
            if (hashCreator.error != null) return XyoResult(XyoError(""))
            val hashCreatorValue = hashCreator.value ?: return XyoResult(XyoError(""))

            val sizeToRead = hashCreatorValue.sizeOfBytesToGetSize
            if (sizeToRead.error != null) return XyoResult(XyoError(""))
            val sizeToReadValue = sizeToRead.value ?: return XyoResult(XyoError(""))
            return XyoResult(hashCreatorValue.readSize(XyoByteArrayReader(byteArray).read(2, sizeToReadValue)).value!! + 2)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val hashCreated = XyoObjectCreator.create(byteArray)
            if (hashCreated.error != null) return XyoResult(XyoError(""))
            val hashCreatedValue = hashCreated.value as? XyoHash ?: return XyoResult(XyoError(""))

            return XyoResult(XyoPreviousHash(hashCreatedValue))
        }
    }
}