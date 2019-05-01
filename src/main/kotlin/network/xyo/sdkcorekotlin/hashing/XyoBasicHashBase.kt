package network.xyo.sdkcorekotlin.hashing

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.security.MessageDigest

/**
 * A base class for fixed size hashes.
 */
abstract class XyoBasicHashBase : XyoHash() {
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
            val item = XyoBuff.newInstance(schema, hash)

                return@async object : XyoBasicHashBase() {
                    override var item: ByteArray = item.bytesCopy
                    override val hash: ByteArray = hash
                    override val allowedOffset: Int = 0
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