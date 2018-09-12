package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
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
class XyoNextPublicKey (publicKey: XyoObject): XyoObject() {
    override val objectInBytes: XyoResult<ByteArray> = publicKey.typed
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult<Int?>(null)

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x07
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(4)

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val keyCreated = XyoObjectProvider.create(byteArray)
            if (keyCreated.error != null) return XyoResult(
                    keyCreated.error ?: XyoError(
                            this.toString(),
                            "Unknown key creation error!"
                    )
            )
            val keyCreatedValue = keyCreated.value ?: return XyoResult(
                    XyoError(this.toString(), "Key created value is null!")
            )

            return XyoResult(XyoNextPublicKey(keyCreatedValue))
        }

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            val publicKeyCreator = XyoObjectProvider.getCreator(byteArray[0], byteArray[1])
            if (publicKeyCreator.error != null) return XyoResult(
                    publicKeyCreator.error ?: XyoError(
                            this.toString(),
                            "Unknown unpacking public key error!"
                    )
            )
            val publicKeyCreatorValue = publicKeyCreator.value ?: return XyoResult(
                    XyoError(this.toString(), "Unpacked public key is null!")
            )

            val sizeToRead = publicKeyCreatorValue.sizeOfBytesToGetSize
            if (sizeToRead.error != null) return XyoResult(
                    sizeToRead.error ?: XyoError(
                            this.toString(),
                            "Unknown public key creator size."
                    )
            )
            val sizeToReadValue = sizeToRead.value ?: return XyoResult(
                    XyoError(this.toString(), "Public key size is null!")
            )
            val publicKeyCreatorSize = publicKeyCreatorValue.readSize(XyoByteArrayReader(byteArray).read(
                    2,
                    sizeToReadValue
            ))
            if (publicKeyCreatorSize.error != null) return XyoResult(
                    publicKeyCreatorSize.error ?: XyoError(
                            this.toString(),
                            "Unknown public key creator size."
                    )
            )
            val publicKeyCreatorSizeValue = publicKeyCreatorSize.value ?: return XyoResult(
                    XyoError(this.toString(), "Creator public key size is null!")
            )
            return XyoResult(publicKeyCreatorSizeValue + 2)
        }
    }
}