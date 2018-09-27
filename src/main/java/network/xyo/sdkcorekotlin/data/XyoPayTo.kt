package network.xyo.sdkcorekotlin.data

/**
 * A object to put in a Bound Witness to identify where to be rewarded.
 *
 * @param address The address to be payed to.
 */
class XyoPayTo (address: XyoObject): XyoObject() {
    override val objectInBytes: ByteArray = address.typed
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = null

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x0b
        override val sizeOfBytesToGetSize: Int? = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val keyCreated = XyoObjectProvider.create(byteArray)
            if (keyCreated != null) {
                return XyoPayTo(keyCreated)
            }
            throw Exception("Can not find address!")
        }

        override fun readSize(byteArray: ByteArray): Int {
            val publicKeyCreator = XyoObjectProvider.getCreator(byteArray[0], byteArray[1])
            if (publicKeyCreator != null) {
                val sizeToRead = publicKeyCreator.sizeOfBytesToGetSize ?: 0
                val publicKeyCreatorSize = publicKeyCreator.readSize(XyoByteArrayReader(byteArray).read(
                        2,
                        sizeToRead
                ))
                return publicKeyCreatorSize + 2
            }
            throw Exception("Can not find creator!")
        }
    }
}