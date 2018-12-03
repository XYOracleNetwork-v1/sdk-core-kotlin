package network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoFromSelf
import network.xyo.sdkcorekotlin.XyoInterpreter
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

/**
 * The base class for RSA Signature
 */
abstract class XyoRsaSignature : XyoInterpreter {

    abstract val signature : ByteArray

    override val schema: XyoObjectSchema
        get() = XyoSchemas.RSA_SIGNATURE

    override val self: ByteArray
        get() = XyoObjectCreator.createObject(schema, signature)

    /**
     * The base class for creating RSA Signatures.
     */
    companion object : XyoFromSelf {

        override fun getInstance(byteArray: ByteArray): XyoInterpreter {
            return object : XyoRsaSignature() {
                override val signature: ByteArray
                    get() = XyoObjectCreator.getObjectValue(byteArray)

                override val self: ByteArray
                    get() = byteArray
            }
        }
    }
}