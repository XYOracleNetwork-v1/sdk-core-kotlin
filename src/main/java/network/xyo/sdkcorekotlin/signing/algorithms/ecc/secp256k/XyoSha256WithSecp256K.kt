package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import java.security.Signature

/**
 * A Xyo Signer using EC with the Secp256K1 curve with SHA256.
 */
class XyoSha256WithSecp256K (privateKey : XyoObject?) : XyoEcSecp256K(privateKey) {
    override fun signData(byteArray: ByteArray): Deferred<XyoObject> {
        return GlobalScope.async {
            return@async XyoSecp256k1Sha256WithEcdsaSignature(signatureInstance.run {
                initSign(keyPair.private)
                update(byteArray)
                sign()
            })
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withECDSA")
        override val key: Byte = 0x01

        override val supportedKeys: Array<ByteArray> = arrayOf(XyoSecp256K1UnCompressedPublicKey.id)
        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoSecp256k1Sha256WithEcdsaSignature.id)

        override fun newInstance(): XyoSigner {
            return XyoSha256WithSecp256K(null)
        }

        override fun newInstance(privateKey: XyoObject): XyoSigner {
            return XyoSha256WithSecp256K(privateKey)
        }
    }
}