package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import java.security.PublicKey
import java.security.Signature

abstract class XyoSigningObjectCreatorVerify : XyoSigningObject.XYOSigningCreator() {
    abstract val signatureInstance : Signature

    override fun verifySign(signature: XyoObject, byteArray: ByteArray, publicKey: XyoObject): Boolean? {
        val encodedPublicKey = publicKey as? PublicKey
        val encodedSignature = signature as? XyoSignature

        if (encodedPublicKey != null && encodedSignature != null) {
            XyoRsaWithSha256.signatureInstance.initVerify(encodedPublicKey)
            XyoRsaWithSha256.signatureInstance.update(byteArray)
            return XyoRsaWithSha256.signatureInstance.verify(encodedSignature.encodedSignature)
        }
        return null
    }
}