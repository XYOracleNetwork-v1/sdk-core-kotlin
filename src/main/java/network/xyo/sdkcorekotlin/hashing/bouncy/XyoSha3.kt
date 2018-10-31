package network.xyo.sdkcorekotlin.hashing.bouncy

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.hashing.XyoHash
import org.bouncycastle.jcajce.provider.digest.SHA3

class XyoSha3(override val hash: ByteArray) : XyoHash() {
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = null

    companion object : XyoHashProvider() {
        override val minor: Byte = 0x0f
        override val sizeOfBytesToGetSize: Int? = 0

        override fun createHash(data: ByteArray): Deferred<XyoHash> = GlobalScope.async {
            val digest = SHA3.DigestSHA3(256)
            digest.update(data)
            return@async XyoSha3(digest.digest())
        }

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoSha3(byteArray)
        }

        override fun readSize(byteArray: ByteArray): Int {
            return 32
        }
    }
}