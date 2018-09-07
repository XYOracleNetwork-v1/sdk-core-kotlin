package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

/**
 * A Xyo Previous Hash heuristic.
 *
 * @major 0x02
 * @minor 0x05
 *
 * @param hash the hash of the previous hash.
 */
open class XyoPreviousHash(val hash: XyoHash) : XyoObject() {
    override val objectInBytes: XyoResult<ByteArray> = hash.typed
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult<Int?>(null)

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x06
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(2)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            val hashCreator = XyoObjectProvider.getCreator(byteArray[0], byteArray[1])
            if (hashCreator.error != null) return XyoResult(
                    hashCreator.error ?: XyoError(
                            this.toString(),
                            "Unknown hash creator error!"
                    )
            )
            val hashCreatorValue = hashCreator.value ?: return XyoResult(
                    XyoError(this.toString(), "Hash creator is null!")
            )

            val sizeToRead = hashCreatorValue.sizeOfBytesToGetSize
            if (sizeToRead.error != null) return XyoResult(
                    sizeToRead.error ?: XyoError(
                            this.toString(),
                            "Unknown hash creator size error!"
                    )
            )
            val sizeToReadValue = sizeToRead.value ?: return XyoResult(XyoError(
                    this.toString(),
                    "Size to read for hash is null!")
            )
            val hashCreatorSize = hashCreatorValue.readSize(XyoByteArrayReader(byteArray).read(
                    2,
                    sizeToReadValue
            ))
            if (hashCreatorSize.error != null) return XyoResult(
                    hashCreatorSize.error ?: XyoError(
                            this.toString(),
                            "Unknown hash creator size value error!"
                    )
            )
            val hashCreatorSizeValue = hashCreatorSize.value ?: return XyoResult(XyoError(
                            this.toString(),
                            "Hash creator size value is null!!")
            )

            return XyoResult(hashCreatorSizeValue + 2)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val hashCreated = XyoObjectProvider.create(byteArray)
            if (hashCreated.error != null) return XyoResult(
                    hashCreated.error ?: XyoError(
                            this.toString(),
                            "Unknown creation error!"
                    )
            )
            val hashCreatedValue = hashCreated.value as? XyoHash ?: return XyoResult(XyoError(
                    this.toString(),
                    "Created value is null!"
            ))

            return XyoResult(XyoPreviousHash(hashCreatedValue))
        }
    }
}