package network.xyo.sdkcorekotlin.hashing

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
    override val objectInBytes: ByteArray = hash.typed
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = null

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x06
        override val sizeOfBytesToGetSize: Int? = 2

        override fun readSize(byteArray: ByteArray): Int {
            val hashCreator = XyoObjectProvider.getCreator(byteArray[0], byteArray[1]) ?: throw Exception("Cant get size!")
            val sizeToRead = hashCreator.sizeOfBytesToGetSize ?: 0
            val hashCreatorSize = hashCreator.readSize(XyoByteArrayReader(byteArray).read(
                    2,
                    sizeToRead
            ))

            return hashCreatorSize + 2
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val hashCreated = XyoObjectProvider.create(byteArray) as XyoHash
            return XyoPreviousHash(hashCreated)
        }
    }
}