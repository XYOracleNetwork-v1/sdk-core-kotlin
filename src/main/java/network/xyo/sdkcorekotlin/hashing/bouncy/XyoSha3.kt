package network.xyo.sdkcorekotlin.hashing.bouncy

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoBasicHashBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import org.bouncycastle.jcajce.provider.digest.SHA3

object XyoSha3 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String
        get() = "SHA3"

    @ExperimentalUnsignedTypes
    override fun createHash(data: ByteArray): Deferred<XyoHash> = GlobalScope.async {
        val digest = SHA3.DigestSHA3(256)
        digest.update(data)
        val hash = digest.digest()

        return@async object : XyoBasicHashBase() {
            override val self: ByteArray = XyoObjectCreator.createObject(this@XyoSha3.schema, hash)
            override val schema: XyoObjectSchema = this@XyoSha3.schema
            override val hash: ByteArray = hash
        }
    }

    @ExperimentalUnsignedTypes
    override val schema: XyoObjectSchema
        get() = XyoSchemas.SHA3
}