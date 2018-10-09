package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import java.security.Signature

/**
 *
 */
class XyoRsaWithSha256 : XyoGeneralRsa (512) {
    override val signature: Signature = signatureInstance

    override fun signData(byteArray: ByteArray): Deferred<XyoObject> {
        return GlobalScope.async {
            signature.initSign(keyPair.private)
            signature.update(byteArray)
            return@async XyoRsaWithSha256Signature(signature.sign())
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withRSA")
        override val key: Byte = 0x08

        override fun newInstance(): XyoSigner {
            return XyoRsaWithSha256()
        }

        override val supportedKeys: Array<ByteArray> = arrayOf(XyoRsaPublicKey.id)
        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoRsaWithSha256Signature.id)
    }
}
