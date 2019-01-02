package network.xyo.sdkcorekotlin.hashing.basic

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import org.bouncycastle.jcajce.provider.digest.SHA3

object XyoSha3 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String = "SHA_3"
    override val schema: XyoObjectSchema = XyoSchemas.SHA_3

    override fun createHash(data: ByteArray): Deferred<XyoHash> = GlobalScope.async {
        val digest = SHA3.DigestSHA3(256)
        digest.update(data)
        val hash = digest.digest()

        return@async object : XyoBasicHashBase() {
            override var item: ByteArray = XyoBuff.newInstance(XyoSha3.schema, hash).bytesCopy
            override val hash: ByteArray = hash
            override val allowedOffset: Int = 0
        }
    }
}