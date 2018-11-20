package network.xyo.sdkcorekotlin.hashing.basic

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.exceptions.XyoCorruptDataException
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.nio.ByteBuffer
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

        @ExperimentalUnsignedTypes
        abstract val schema : XyoObjectSchema

        @ExperimentalUnsignedTypes
        override fun createHash (data: ByteArray) : Deferred<XyoHash> {
            return GlobalScope.async {
            val hash = hash(data)
            val item = XyoObjectCreator.createObject(schema, hash)

                return@async object : XyoBasicHashBase() {
                    override val self: ByteArray = item
                    override val schema: XyoObjectSchema = this@XyoBasicHashBaseProvider.schema
                    override val hash: ByteArray = hash
                }
            }
        }

        private fun hash(data: ByteArray): ByteArray {
            return MessageDigest.getInstance(standardDigestKey).digest(data)
        }

        override fun getInstance(byteArray: ByteArray): XyoInterpreter {
            return object : XyoBasicHashBase() {
                override val self: ByteArray = byteArray

                @ExperimentalUnsignedTypes
                override val schema: XyoObjectSchema = this@XyoBasicHashBaseProvider.schema

                override val hash: ByteArray
                    get() = self.copyOfRange(2, self.size)

            }
        }
    }

    companion object {
        @ExperimentalUnsignedTypes
        fun createHashType (schema: XyoObjectSchema, standardDigestKey: String) : XyoBasicHashBaseProvider {
            return object : XyoBasicHashBaseProvider() {
                override val schema: XyoObjectSchema = schema
                override val standardDigestKey: String = standardDigestKey
            }
        }
    }
}