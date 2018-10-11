package network.xyo.sdkcorekotlin.data

open class XyoGenericItem (override val objectInBytes: ByteArray) : XyoObject() {
    override val id: ByteArray
        get() = byteArrayOf(0x0f, 0x0f)

    override val sizeIdentifierSize: Int?
        get() = 4


    companion object : XyoObjectProvider() {
        override val major: Byte
            get() = 0x0f

        override val minor: Byte
            get() = 0x0f

        override val sizeOfBytesToGetSize: Int?
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoGenericItem(XyoByteArrayReader(byteArray).read(4, byteArray.size - 4))
        }

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }
    }
}