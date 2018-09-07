package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import java.security.PublicKey
import java.security.Signature

/**
 * A base class for verifying signatures that comply to the standard Java Signature object.
 */
abstract class XyoSigningObjectCreatorVerify : XyoSigner.XyoSignerProvider() {
    /**
     * The instance of a standard Java Signature object to use toi very the signature.
     */
    abstract val signatureInstance : Signature

    override fun verifySign(signature: XyoObject,
                            byteArray: ByteArray,
                            publicKey: XyoObject): Deferred<XyoResult<Boolean>> {

        return async {
            val encodedPublicKey = publicKey as? PublicKey
            val encodedSignature = signature as? XyoSignature

            if (encodedPublicKey != null && encodedSignature != null) {
                signatureInstance.initVerify(encodedPublicKey)
                signatureInstance.update(byteArray)
                return@async XyoResult(
                        signatureInstance.verify(encodedSignature.encodedSignature)
                )
            }
            return@async XyoResult<Boolean>(XyoError(
                    this.toString(),
                    "Signature or publicKey can not be casted!"
                )
            )
        }
    }
}