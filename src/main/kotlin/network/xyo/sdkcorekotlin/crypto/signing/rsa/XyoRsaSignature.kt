package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * The base class for RSA Signature
 */
class XyoRsaSignature : XyoObjectStructure {

    constructor(bytes: ByteArray): super(bytes) {
        this.bytes = newInstance(XyoSchemas.RSA_SIGNATURE, this@XyoRsaSignature.signature).bytesCopy
    }

    val signature : ByteArray
        get() = valueCopy

    /**
     * The base class for creating RSA Signatures.
     */
    companion object : XyoInterpret {

        override fun getInstance(byteArray: ByteArray): XyoRsaSignature {
            return XyoRsaSignature(byteArray)
        }
    }
}