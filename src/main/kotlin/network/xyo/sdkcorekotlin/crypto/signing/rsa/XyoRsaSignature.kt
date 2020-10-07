package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.ObjectId
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * The base class for RSA Signature
 */
@ExperimentalStdlibApi
class XyoRsaSignature : XyoObjectStructure {

    constructor(signature: ByteArray): super(ObjectId.RsaSignature, false, false, signature) {}
}