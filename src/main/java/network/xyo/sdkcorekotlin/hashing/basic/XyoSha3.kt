package network.xyo.sdkcorekotlin.hashing.basic

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import org.bouncycastle.jcajce.provider.digest.SHA3

object XyoSha3 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String = "SHA3"

    override fun createHash(data: ByteArray): Deferred<XyoHash> = GlobalScope.async {
        val digest = SHA3.DigestSHA3(256)
        digest.update(data)
        val hash = digest.digest()

        return@async object : XyoBasicHashBase() {
            override val self: ByteArray = XyoObjectCreator.createObject(XyoSha3.schema, hash)
            override val schema: XyoObjectSchema = XyoSha3.schema
            override val hash: ByteArray = hash
        }
    }

    override val schema: XyoObjectSchema
        get() = XyoSchemas.SHA3
}