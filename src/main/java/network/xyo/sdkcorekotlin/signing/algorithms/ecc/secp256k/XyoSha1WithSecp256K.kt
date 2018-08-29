package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha1WithEcdsaSignature
import java.security.Signature

class XyoSha1WithSecp256K : XyoEcSecp256K() {
    override fun signData(byteArray: ByteArray): Deferred<XyoResult<XyoObject>> {
        return async {
            return@async XyoResult<XyoObject>(XyoSha1WithEcdsaSignature(signatureInstance.run {
                initSign(keyPair.private)
                update(byteArray)
                sign()
            }))
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA1withECDSA")

        override fun newInstance(): XyoResult<XyoSigningObject> {
            return XyoResult(XyoSha256WithSecp256K())
        }

        override val key: Byte
            get() = 0x02
    }
}