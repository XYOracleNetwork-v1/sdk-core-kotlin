package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.bouncycastle.jcajce.provider.digest.SHA3

/**
 * A SHA-3 32 byte (256 bit) hash. Sometimes known as a Keccak hash.
 */
object XyoSha3 : XyoBasicHashBase.XyoBasicHashBaseProvider() {
    override val standardDigestKey: String = "SHA_3"
    override val schema: XyoObjectSchema = XyoSchemas.SHA_3

    override suspend fun createHash(data: ByteArray): XyoHash {
        val digest = SHA3.DigestSHA3(256)
        digest.update(data)
        val hash = digest.digest()

        return object : XyoBasicHashBase(XyoObjectStructure.newInstance(XyoSha3.schema, hash).bytesCopy) {
            override val hash: ByteArray = hash
        }
    }
}