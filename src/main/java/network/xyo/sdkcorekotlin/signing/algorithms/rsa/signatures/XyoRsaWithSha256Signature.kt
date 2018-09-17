package network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures

import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaSignature

/**
 * An encoded RSA signature with SHA56.
 *
 * @major 0x05
 * @minor 0x08
 */
class XyoRsaWithSha256Signature(rawSignature: ByteArray) : XyoRsaSignature(rawSignature) {
    override val id: ByteArray = byteArrayOf(major, minor)

    companion object : XyoRsaSignatureProvider() {
        override val minor: Byte = 0x08
    }
}