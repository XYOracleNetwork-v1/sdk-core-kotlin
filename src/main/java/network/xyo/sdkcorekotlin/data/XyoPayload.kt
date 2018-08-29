package network.xyo.sdkcorekotlin.data

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import java.nio.ByteBuffer

class XyoPayload(val signedPayload : XyoMultiTypeArrayInt,
                 val unsignedPayload : XyoMultiTypeArrayInt) : XyoObject() {

    override val data: XyoResult<ByteArray>
        get() = makeEncoded()

    override val id: XyoResult<ByteArray>
        get() = XyoResult(byteArrayOf(major, minor))

    override val sizeIdentifierSize: XyoResult<Int?>
        get() = XyoResult(4)

    private fun makeEncoded () : XyoResult<ByteArray> {
        val merger = XyoByteArraySetter(2)
        val signedPayloadUntyped = signedPayload.untyped
        val unsignedPayloadUntyped = unsignedPayload.untyped

        if (unsignedPayloadUntyped.error == null && unsignedPayloadUntyped.value != null) {
            if (signedPayloadUntyped.error == null && signedPayloadUntyped.value != null) {
                merger.add(signedPayloadUntyped.value!!, 0)
                merger.add(unsignedPayload.untyped.value!!, 1)
                return XyoResult(merger.merge())
            }
            return XyoResult(XyoError(""))
        }
        return XyoResult(XyoError(""))
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x04

        override val sizeOfBytesToGetSize: XyoResult<Int?>
            get() = XyoResult(4)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(ByteBuffer.wrap(byteArray).int)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val reader = XyoByteArrayReader(byteArray)
            val signedPayloadSize = ByteBuffer.wrap(reader.read(4, 4)).int
            val unsignedPayloadSize =  ByteBuffer.wrap(reader.read(4 + signedPayloadSize, 4)).int

            val signedPayload = reader.read(4, signedPayloadSize)
            val unsignedPayload = reader.read(4 + signedPayloadSize, unsignedPayloadSize)

            val signedPayloadCreated = XyoMultiTypeArrayInt.createFromPacked(signedPayload)
            val unsignedPayloadCreated = XyoMultiTypeArrayInt.createFromPacked(unsignedPayload)

            return XyoResult(XyoPayload(signedPayloadCreated.value as XyoMultiTypeArrayInt, unsignedPayloadCreated.value as XyoMultiTypeArrayInt))
        }
    }
}