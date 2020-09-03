package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.security.MessageDigest

/**
 * A base class for fixed size hashes.
 */
abstract class XyoBasicHashBase(byteArray: ByteArray) : XyoHash(byteArray) {
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
            val item = XyoObjectStructure.newInstance(schema, hash)

            return object : XyoBasicHashBase(item.bytesCopy) {
                override val hash: ByteArray = hash
            }
        }
    }

    companion object {
        fun createHashType (schema: XyoObjectSchema, standardDigestKey: String) : XyoBasicHashBaseProvider {
            return XyoBasicHashBaseProvider(standardDigestKey, schema)
        }
    }
}