package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.security.MessageDigest

/**
 * A base class for fixed size hashes.
 */
open class XyoBasicHashBase(byteArray: ByteArray, hash: ByteArray) : XyoHash(hash, byteArray) {
    /**
     * A base class for creating Standard Java hashes supported by MessageDigest.
     */
    open class XyoBasicHashBaseProvider(val standardDigestKey: String, val schema : XyoObjectSchema) : XyoHashProvider() {

        /**
         * The MessageDigest instance key. e.g. "SHA-256"
         */

        private fun generateHash(data: ByteArray): ByteArray {
            return MessageDigest.getInstance(standardDigestKey).digest(data)
        }

        override suspend fun createHash (data: ByteArray) : XyoHash {
            val hash = generateHash(data)
            val item = newInstance(schema, hash)

            return XyoBasicHashBase(item.bytesCopy, hash)
        }
    }

    companion object {
        fun createHashType (schema: XyoObjectSchema, standardDigestKey: String) : XyoBasicHashBaseProvider {
            return XyoBasicHashBaseProvider(standardDigestKey, schema)
        }
    }
}