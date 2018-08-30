package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import java.security.Signature

class XyoRsaWithSha256 : XyoGeneralRsa (512) {
    override val signature: Signature = signatureInstance

    override fun signData(byteArray: ByteArray): Deferred<XyoResult<XyoObject>> {
        return async {
            signature.initSign(keyPair.private)
            signature.update(byteArray)
            return@async XyoResult<XyoObject>(XyoRsaWithSha256Signature(signature.sign()))
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withRSA")
        override val key: Byte = 0x08

        override fun newInstance(): XyoResult<XyoSigner> {
            return XyoResult(XyoRsaWithSha256())
        }
    }
}
