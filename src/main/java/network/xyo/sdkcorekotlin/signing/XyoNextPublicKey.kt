package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

/**
 * The next public key heuristic.
 *
 * @major 0x02
 * @minor 0x07
 *
 * @param publicKey the public key that is the next public key.
 */
class XyoNextPublicKey (val publicKey: XyoObject): XyoObject() {
    override val objectInBytes: ByteArray = publicKey.typed
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = null

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x07
        override val sizeOfBytesToGetSize: Int? = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val keyCreated = XyoObjectProvider.create(byteArray)
            if (keyCreated != null) {
                return XyoNextPublicKey(keyCreated)
            }
            throw Exception("Can not find key!")
        }

        override fun readSize(byteArray: ByteArray): Int {
            val publicKeyCreator = XyoObjectProvider.getCreator(byteArray[0], byteArray[1])
            if (publicKeyCreator != null) {
                val sizeToRead = publicKeyCreator.sizeOfBytesToGetSize ?: 0
                val publicKeyCreatorSize = publicKeyCreator.readSize(XyoByteArrayReader(byteArray).read(2, sizeToRead))
                return publicKeyCreatorSize + 2
            }
            throw Exception("Can not find creator!")
        }
    }
}