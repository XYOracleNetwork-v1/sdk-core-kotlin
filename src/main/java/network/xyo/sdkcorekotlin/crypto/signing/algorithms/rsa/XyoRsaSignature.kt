package network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * The base class for RSA Signature
 */
abstract class XyoRsaSignature : XyoBuff() {

    open val signature : ByteArray
        get() = valueCopy

    override val allowedOffset: Int
        get() = 0

    override var item: ByteArray
        get() = XyoBuff.newInstance(schema, signature).bytesCopy
        set(value) {}

    override val schema: XyoObjectSchema
        get() = XyoSchemas.RSA_SIGNATURE



    /**
     * The base class for creating RSA Signatures.
     */
    companion object : XyoFromSelf {

        override fun getInstance(byteArray: ByteArray): XyoRsaSignature {
            return object : XyoRsaSignature() {
                override var item: ByteArray = byteArray
            }
        }
    }
}