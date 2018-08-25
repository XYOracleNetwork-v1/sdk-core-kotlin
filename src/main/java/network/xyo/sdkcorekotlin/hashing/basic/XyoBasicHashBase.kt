package network.xyo.sdkcorekotlin.hashing.basic

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.hashing.XyoHash
import java.security.MessageDigest

abstract class XyoBasicHashBase (pastHash : ByteArray): XyoHash() {
    private val mHash = pastHash

    override val hash: ByteArray
        get() = mHash

    override val sizeIdentifierSize: Int?
        get() = null

    abstract class XyoBasicHashBaseCreator : XyoHashCreator() {
        abstract val standardDigestKey : String

        override val sizeOfSize: Int?
            get() = null

        override fun createHash (data: ByteArray) : Deferred<XyoResult<XyoHash>> {
            return async {
                return@async XyoResult<XyoHash>(object : XyoBasicHashBase(hash(data)) {
                    override val id: ByteArray
                        get() = byteArrayOf(major, minor)
                })
            }
        }

        private fun hash(data: ByteArray): ByteArray {
            return MessageDigest.getInstance(standardDigestKey).digest(data)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoHash {
            val hash = XyoByteArrayReader(byteArray).read(0, byteArray.size)
            return object : XyoBasicHashBase(hash) {
                override val id: ByteArray
                    get() = byteArrayOf(major, minor)
            }
        }
    }
}