package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

/**
 * The base class for RSA Signature
 */
abstract class XyoRsaSignature : XyoBuff() {

    open val signature : ByteArray
        get() = valueCopy

    override val allowedOffset: Int
        get() = 0

    override var item: ByteArray = byteArrayOf()
        get() = XyoBuff.newInstance(XyoSchemas.RSA_SIGNATURE, signature).bytesCopy

    /**
     * The base class for creating RSA Signatures.
     */
    companion object : XyoInterpret {

        override fun getInstance(byteArray: ByteArray): XyoRsaSignature {
            return object : XyoRsaSignature() {
                override var item: ByteArray = byteArray
            }
        }
    }
}