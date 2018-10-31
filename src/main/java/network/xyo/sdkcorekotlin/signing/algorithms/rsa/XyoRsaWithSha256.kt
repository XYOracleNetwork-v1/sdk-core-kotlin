package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Signature

/**
 *
 */
class XyoRsaWithSha256 (privateKey: XyoObject?) : XyoGeneralRsa (1024, privateKey) {
    override val signature: Signature
        get() = signatureInstance

    override fun signData(byteArray: ByteArray): Deferred<XyoObject> {
        return GlobalScope.async {
            signature.initSign(keyPair.private)
            signature.update(byteArray)
            return@async XyoRsaWithSha256Signature(signature.sign())
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withRSA", BouncyCastleProvider())
        override val key: Byte = 0x08

        override fun newInstance(): XyoSigner {
            return XyoRsaWithSha256(null)
        }

        override fun newInstance(privateKey: XyoObject): XyoSigner {
            return XyoRsaWithSha256(privateKey)
        }

        override val supportedKeys: Array<ByteArray> = arrayOf(XyoRsaPublicKey.id)
        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoRsaWithSha256Signature.id)
    }
}
