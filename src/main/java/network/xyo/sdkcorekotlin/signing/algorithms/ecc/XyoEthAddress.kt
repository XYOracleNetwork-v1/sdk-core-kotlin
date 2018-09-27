package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider

/**
 * A Ethereum Address to be used when referencing ethereum accounts. For example in the XyoPayTo.
 *
 * @param ethAddress The Ethereum Address.
 */
class XyoEthAddress (val ethAddress: ByteArray) : XyoObject() {
    override val id: ByteArray = byteArrayOf(major, minor)
    override val objectInBytes: ByteArray = ethAddress

    override val sizeIdentifierSize: Int?
        get() = null

    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x0c

        override val sizeOfBytesToGetSize: Int? = null

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoEthAddress(byteArray)
        }

        override fun readSize(byteArray: ByteArray): Int {
            return 22
        }
    }
}