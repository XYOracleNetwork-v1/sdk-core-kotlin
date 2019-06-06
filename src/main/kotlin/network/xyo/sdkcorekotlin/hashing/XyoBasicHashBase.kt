package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
    abstract class XyoBasicHashBaseProvider : XyoHashProvider() {
        /**
         * The MessageDigest instance key. e.g. "SHA-256"
         */
        abstract val standardDigestKey : String

        abstract val schema : XyoObjectSchema

        override fun createHash (data: ByteArray) : Deferred<XyoHash> {
            return GlobalScope.async {
            val hash = hash(data)
            val item = XyoObjectStructure.newInstance(schema, hash)

                return@async object : XyoBasicHashBase(item.bytesCopy) {
                    override val hash: ByteArray = hash
                }
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