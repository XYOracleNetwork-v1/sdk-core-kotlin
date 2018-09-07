package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256kSha1WithEcdsaSignature
import java.security.Signature

/**
 * A Xyo Signer using EC with the Secp256K curve with SHA1.
 */
class XyoSha1WithSecp256K : XyoEcSecp256K() {
    override fun signData(byteArray: ByteArray): Deferred<XyoResult<XyoObject>> {
        return async {
            return@async XyoResult<XyoObject>(XyoSecp256kSha1WithEcdsaSignature(signatureInstance.run {
                initSign(keyPair.private)
                update(byteArray)
                sign()
            }))
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA1withECDSA")
        override val key: Byte = 0x02

        override fun newInstance(): XyoResult<XyoSigner> {
            return XyoResult(XyoSha256WithSecp256K())
        }
    }
}