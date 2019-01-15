package network.xyo.sdkcorekotlin.crypto.signing.stub

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.crypto.signing.XyoPrivateKey
import network.xyo.sdkcorekotlin.crypto.signing.XyoPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.security.PrivateKey

class XyoStubSigner : XyoSigner() {
    override val privateKey: XyoPrivateKey
        get() = object : XyoPrivateKey() {
            override fun getAlgorithm(): String {
                return "stub"
            }

            override fun getEncoded(): ByteArray {
                return byteArrayOf(0x00)
            }

            override val allowedOffset: Int = 0

            override var item: ByteArray = XyoBuff.newInstance(XyoSchemas.BLOB, byteArrayOf(0x00)).bytesCopy

            override fun getFormat(): String {
                return "stub"
            }
        }

    override val publicKey: XyoPublicKey
        get() = object : XyoPublicKey() {
            override val allowedOffset: Int
                get() = 0

            override var item: ByteArray = byteArrayOf()
                get() = XyoBuff.newInstance(XyoSchemas.STUB_PUBLIC_KEY, byteArrayOf(0x00)).bytesCopy

            override fun getAlgorithm(): String {
                return "stub"
            }

            override fun getEncoded(): ByteArray {
                return item
            }

            override fun getFormat(): String {
                return "stub"
            }
        }


    override fun signData(byteArray: ByteArray): Deferred<XyoBuff> = GlobalScope.async {
        return@async XyoBuff.newInstance(XyoSchemas.STUB_SIGNATURE, byteArrayOf(0x00))
    }
}