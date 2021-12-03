package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * The base class for RSA Signature
 */
abstract class XyoRsaSignature : XyoObjectStructure(byteArrayOf(), 0) {

    open val signature : ByteArray
        get() = valueCopy

    override var item: ByteArray = byteArrayOf()
        get() = XyoObjectStructure.newInstance(XyoSchemas.RSA_SIGNATURE, this@XyoRsaSignature.signature).bytesCopy

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