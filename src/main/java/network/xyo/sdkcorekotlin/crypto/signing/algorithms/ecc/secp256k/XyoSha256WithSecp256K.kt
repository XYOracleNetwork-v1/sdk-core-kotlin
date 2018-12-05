package network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoEcdsaSignature
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import network.xyo.sdkcorekotlin.hashing.basic.XyoBasicHashBase
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.jce.interfaces.ECPrivateKey
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature


/**
 * A Xyo Signer using EC with the Secp256K1 curve with SHA256.
 */
class XyoSha256WithSecp256K (privateKey : ECPrivateKey?) : XyoEcSecp256K1(privateKey) {

    override fun signData(byteArray: ByteArray): Deferred<XyoBuff> {
        return GlobalScope.async {
            signatureInstance.initSign(keyPair.private)
            signatureInstance.update(byteArray)
            signatureInstance.sign()

            val pam = ECPrivateKeyParameters((keyPair.private as XyoEcPrivateKey).d, ecDomainParameters)

            val signer = ECDSASigner()
            signer.init(true, pam)
            val sig = signer.generateSignature(hashData(byteArray))

            return@async XyoEcdsaSignature(sig[0], sig[1])
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider())
        override val key: Byte = 0x01
        override val supportedKeys: Array<ByteArray> = arrayOf(XyoSchemas.EC_PUBLIC_KEY.header)

        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoSchemas.EC_PRIVATE_KEY.header)

        override fun newInstance(): XyoSigner {
            return XyoSha256WithSecp256K(null)
        }

        override fun newInstance(privateKey: ByteArray): XyoSigner {
            return XyoSha256WithSecp256K(XyoEcPrivateKey.getInstance(privateKey, XyoEcSecp256K1.ecSpec))
        }

        override fun verifySign(signature: XyoBuff, byteArray: ByteArray, publicKey: XyoBuff): Deferred<Boolean> = GlobalScope.async {
            val signer = ECDSASigner()

            val ecDomainParameters = ECDomainParameters(ecCurve.curve, ecCurve.g, ecCurve.n)
            signer.init(false, ECPublicKeyParameters(ecCurve.curve.createPoint((publicKey as XyoUncompressedEcPublicKey).x, publicKey.y), ecDomainParameters))


            val ecSig = XyoEcdsaSignature.getInstance(signature.bytesCopy)

            val r = ecSig.r
            val s = ecSig.s

            return@async signer.verifySignature(hashData(byteArray), r, s)

        }

        private fun hashData (byteArray: ByteArray) : ByteArray {
            return MessageDigest.getInstance("SHA-256").digest(byteArray)
        }
    }
}