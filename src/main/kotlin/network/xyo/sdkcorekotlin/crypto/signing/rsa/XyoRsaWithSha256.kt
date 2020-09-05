package network.xyo.sdkcorekotlin.crypto.signing.rsa

import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Signature

/**
 * The base class for RSA Signature with SHA256
 */
class XyoRsaWithSha256 (privateKey: XyoRsaPrivateKey?) : XyoGeneralRsa (1024, privateKey) {
    override val signature: Signature
        get() = signatureInstance

    override suspend fun signData(byteArray: ByteArray): XyoObjectStructure {
        signature.initSign(keyPair.private)
        signature.update(byteArray)
        return XyoRsaSignature(this@XyoRsaWithSha256.signature.sign())
    }

    companion object : XyoSigner.XyoSignerProvider() {
        private val signatureInstance: Signature = Signature.getInstance("SHA256withRSA", BouncyCastleProvider())
        override val key: Byte = 0x08

        override fun newInstance(): XyoRsaWithSha256 {
            return XyoRsaWithSha256(null)
        }

        override fun newInstance(privateKey: ByteArray): XyoRsaWithSha256 {
            return XyoRsaWithSha256(XyoRsaPrivateKey.getInstance(privateKey))
        }

        override val supportedKeys: Array<Byte> = arrayOf(XyoSchemas.RSA_PUBLIC_KEY.id)

        override val supportedSignatures: Array<Byte> = arrayOf(XyoSchemas.RSA_SIGNATURE.id)

        override suspend fun verifySign(signature: XyoObjectStructure, byteArray: ByteArray, publicKey: XyoObjectStructure): Boolean {
            signatureInstance.initVerify(XyoRsaPublicKey.getInstance(publicKey.bytesCopy))
            signatureInstance.update(byteArray)
            return signatureInstance.verify(signature.valueCopy)
        }
    }
}
