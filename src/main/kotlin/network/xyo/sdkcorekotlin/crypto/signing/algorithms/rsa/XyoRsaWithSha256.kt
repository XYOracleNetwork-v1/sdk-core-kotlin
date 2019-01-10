package network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.PublicKey
import java.security.Signature

/**
 *
 */
class XyoRsaWithSha256 (privateKey: XyoRsaPrivateKey?) : XyoGeneralRsa (1024, privateKey) {
    override val signature: Signature
        get() = signatureInstance

    override fun signData(byteArray: ByteArray): Deferred<XyoBuff> {
        return GlobalScope.async {
            signature.initSign(keyPair.private)
            signature.update(byteArray)
            return@async object : XyoRsaSignature() {
                override val signature: ByteArray = this@XyoRsaWithSha256.signature.sign()
            }
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withRSA", BouncyCastleProvider())
        override val key: Byte = 0x08

        override fun newInstance(): XyoRsaWithSha256 {
            return XyoRsaWithSha256(null)
        }

        override fun newInstance(privateKey: ByteArray): XyoRsaWithSha256 {
            return XyoRsaWithSha256(XyoRsaPrivateKey.getInstance(privateKey))
        }

        override val supportedKeys: Array<Byte> = arrayOf(XyoSchemas.RSA_PUBLIC_KEY.id)

        override val supportedSignatures: Array<Byte> = arrayOf(XyoSchemas.RSA_SIGNATURE.id)

        override fun verifySign(signature: XyoBuff, byteArray: ByteArray, publicKey: XyoBuff): Deferred<Boolean> {
            return GlobalScope.async {
                signatureInstance.initVerify(XyoRsaPublicKey.getInstance(publicKey.bytesCopy))
                signatureInstance.update(byteArray)
                return@async signatureInstance.verify(signature.valueCopy)
            }
        }
    }
}
