package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.bouncycastle.jcajce.provider.digest.SHA3

/**
 * A SHA-3 32 byte (256 bit) hash. Sometimes known as a Keccak hash.
 */
@ExperimentalStdlibApi
object XyoSha3 : XyoBasicHashBase.XyoBasicHashBaseProvider("SHA_3", XyoSchemas.SHA_3) {
    override suspend fun createHash(data: ByteArray): XyoHash {
        val digest = SHA3.DigestSHA3(256)
        digest.update(data)
        val hash = digest.digest()

        return XyoBasicHashBase(XyoObjectStructure.newInstance(XyoSha3.schema, hash).bytesCopy, hash)
    }
}