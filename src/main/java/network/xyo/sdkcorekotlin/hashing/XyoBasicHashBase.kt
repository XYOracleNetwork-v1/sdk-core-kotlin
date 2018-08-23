package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.data.XyoByteArrayReader
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

        override fun createHash(data: ByteArray): XyoHash {
            return object : XyoBasicHashBase (hash(data)) {
                override val id: ByteArray
                get() = byteArrayOf(major, minor)
            }
        }

        override fun hash(data: ByteArray): ByteArray {
            return MessageDigest.getInstance(standardDigestKey).digest(data)
        }

        override fun createFromPacked(byteArray: ByteArray): XyoHash {
            val hash = XyoByteArrayReader(byteArray).read(2, byteArray.size - 2)
            return object : XyoBasicHashBase (hash) {
                override val id: ByteArray
                    get() = byteArrayOf(byteArray[0], byteArray[1])
            }
        }
    }
}