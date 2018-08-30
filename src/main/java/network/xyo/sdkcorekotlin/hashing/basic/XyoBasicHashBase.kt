package network.xyo.sdkcorekotlin.hashing.basic

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.hashing.XyoHash
import java.security.MessageDigest

abstract class XyoBasicHashBase (pastHash : ByteArray): XyoHash() {
    override val hash: ByteArray = pastHash
    override val sizeIdentifierSize: XyoResult<Int?> = XyoResult<Int?>(null)

    abstract class XyoBasicHashBaseProvider : XyoHashProvider() {
        abstract val standardDigestKey : String
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(0)

        override fun createHash (data: ByteArray) : Deferred<XyoResult<XyoHash>> {
            return async {
                return@async XyoResult<XyoHash>(object : XyoBasicHashBase(hash(data)) {
                    override val id: XyoResult<ByteArray>
                        get() = XyoResult(byteArrayOf(major, minor))
                })
            }
        }

        private fun hash(data: ByteArray): ByteArray {
            return MessageDigest.getInstance(standardDigestKey).digest(data)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val hash = XyoByteArrayReader(byteArray).read(0, byteArray.size)
            return XyoResult(object : XyoBasicHashBase(hash) {
                override val id: XyoResult<ByteArray>
                    get() = XyoResult(byteArrayOf(major, minor))
            })
        }
    }
}