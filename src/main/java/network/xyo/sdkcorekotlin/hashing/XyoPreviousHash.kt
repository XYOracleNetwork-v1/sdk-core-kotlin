package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator

open class XyoPreviousHash(val hash: XyoHash) : XyoObject() {
    override val data: ByteArray
        get() = hash.typed

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = null

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x06

        override val sizeOfBytesToGetSize: Int
            get() = 8

        override fun readSize(byteArray: ByteArray): Int {
            val hashCreator = XyoObjectCreator.getCreator(byteArray[0], byteArray[1])
            if (hashCreator != null) {
                val sizeToRead = hashCreator.sizeOfBytesToGetSize
                return hashCreator.readSize(XyoByteArrayReader(byteArray).read(2, sizeToRead))
            }
            throw Exception()
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val hashCreated = XyoObjectCreator.create(byteArray) as? XyoHash
            if (hashCreated != null){
                return XyoPreviousHash(hashCreated )
            }
            throw Exception()
        }
    }
}