package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.security.MessageDigest

/**
 * A base class for fixed size hashes.
 */
abstract class XyoBasicHashBase(byteArray: ByteArray) : XyoHash(byteArray) {
    /**
     * A base class for creating Standard Java hashes supported by MessageDigest.
     */
    abstract class XyoBasicHashBaseProvider : XyoHashProvider() {
        /**
         * The MessageDigest instance key. e.g. "SHA-256"
         */
        abstract val standardDigestKey : String

        abstract val schema : XyoObjectSchema

        override suspend fun createHash (data: ByteArray) : XyoHash {
            val hash = hash(data)
            val item = newInstance(schema, hash)

            return object : XyoBasicHashBase(item.bytesCopy) {
                override val hash: ByteArray = hash
            }
        }

        private fun hash(data: ByteArray): ByteArray {
            return MessageDigest.getInstance(standardDigestKey).digest(data)
        }
    }

    companion object {
        fun createHashType (schema: XyoObjectSchema, standardDigestKey: String) : XyoBasicHashBaseProvider {
            return object : XyoBasicHashBaseProvider() {
                override val schema: XyoObjectSchema = schema
                override val standardDigestKey: String = standardDigestKey
            }
        }
    }
}