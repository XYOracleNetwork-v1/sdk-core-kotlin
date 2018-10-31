package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoObject
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.ECPublicKey

/**
 * A base class for verifying signaturePacking that comply to the standard Java Signature object.
 */
abstract class XyoSigningObjectCreatorVerify : XyoSigner.XyoSignerProvider() {
    /**
     * The instance of a standard Java Signature object to use toi very the signature.
     */
    abstract val signatureInstance : Signature

    override fun verifySign(signature: XyoObject,
                            byteArray: ByteArray,
                            publicKey: XyoObject): Deferred<Boolean> {

        return GlobalScope.async {
            val encodedPublicKey = publicKey as? PublicKey
            val encodedSignature = signature as? XyoSignature

            if (encodedPublicKey != null && encodedSignature != null) {
                val test = encodedPublicKey as ECPublicKey




                signatureInstance.initVerify(encodedPublicKey)
                signatureInstance.update(byteArray)
                return@async signatureInstance.verify(encodedSignature.encodedSignature)
            }
            throw Exception("Keys can not be casted!")
        }
    }
}