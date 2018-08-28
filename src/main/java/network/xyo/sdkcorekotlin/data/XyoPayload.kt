package network.xyo.sdkcorekotlin.data

import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import java.nio.ByteBuffer

class XyoPayload(val signedPayload : XyoMultiTypeArrayInt,
                 val unsignedPayload : XyoMultiTypeArrayInt) : XyoObject() {

    override val data: ByteArray
        get() = makeEncoded()

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = 4

    private fun makeEncoded () : ByteArray {
        val merger = XyoByteArraySetter(2)
        merger.add(signedPayload.untyped, 0)
        merger.add(unsignedPayload.untyped, 1)
        return merger.merge()
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x04

        override val sizeOfBytesToGetSize: Int
            get() = 4

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).int
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val reader = XyoByteArrayReader(byteArray)
            val signedPayloadSize = ByteBuffer.wrap(reader.read(4, 4)).int
            val unsignedPayloadSize =  ByteBuffer.wrap(reader.read(4 + signedPayloadSize, 4)).int

            val signedPayload = reader.read(4, signedPayloadSize)
            val unsignedPayload = reader.read(4 + signedPayloadSize, unsignedPayloadSize)

            val signedPayloadCreated = XyoMultiTypeArrayInt.createFromPacked(signedPayload)
            val unsignedPayloadCreated = XyoMultiTypeArrayInt.createFromPacked(unsignedPayload)

            return XyoPayload(signedPayloadCreated, unsignedPayloadCreated)
        }
    }
}