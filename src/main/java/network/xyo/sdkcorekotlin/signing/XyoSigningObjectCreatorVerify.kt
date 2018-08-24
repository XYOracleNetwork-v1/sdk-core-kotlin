package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import java.security.PublicKey
import java.security.Signature

abstract class XyoSigningObjectCreatorVerify : XyoSigningObject.XYOSigningCreator() {
    abstract val signatureInstance : Signature

    override fun verifySign(signature: XyoObject, byteArray: ByteArray, publicKey: XyoObject): Deferred<XyoResult<Boolean>> {
        return async {
            val encodedPublicKey = publicKey as? PublicKey
            val encodedSignature = signature as? XyoSignature

            if (encodedPublicKey != null && encodedSignature != null) {
                XyoRsaWithSha256.signatureInstance.initVerify(encodedPublicKey)
                XyoRsaWithSha256.signatureInstance.update(byteArray)
                return@async XyoResult(XyoRsaWithSha256.signatureInstance.verify(encodedSignature.encodedSignature))
            }
            return@async XyoResult<Boolean>(XyoError("Invalid or signature!"))
        }
    }
}