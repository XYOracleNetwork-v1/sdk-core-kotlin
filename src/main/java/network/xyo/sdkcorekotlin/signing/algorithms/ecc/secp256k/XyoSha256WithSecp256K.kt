package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha1WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import org.bouncycastle.asn1.x9.ECNamedCurveTable
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Signature
import org.bouncycastle.asn1.x509.ObjectDigestInfo.publicKey
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.getCurve
import org.bouncycastle.crypto.params.ECPublicKeyParameters



/**
 * A Xyo Signer using EC with the Secp256K1 curve with SHA256.
 */
class XyoSha256WithSecp256K (privateKey : XyoObject?) : XyoEcSecp256K(privateKey) {
    override fun signData(byteArray: ByteArray): Deferred<XyoObject> {
        return GlobalScope.async {
            val ecDomainParameters = ECDomainParameters(ecCurve.getCurve(), ecCurve.getG(), ecCurve.getN())
            signatureInstance.initSign(keyPair.private)
            signatureInstance.update(byteArray)
            signatureInstance.sign()

            val pam = ECPrivateKeyParameters((keyPair.private as XyoEcPrivateKey).s, ecDomainParameters)

            val signer = ECDSASigner()
            signer.init(true, pam)
            val sig = signer.generateSignature(XyoSha256.createHash(byteArray).await().objectInBytes)

            return@async XyoSecp256k1Sha256WithEcdsaSignature(sig[0], sig[1])
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        private val ecCurve = ECNamedCurveTable.getByName("secp256k1")

        override val signatureInstance: Signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider())
        override val key: Byte = 0x01

        override val supportedKeys: Array<ByteArray> = arrayOf(XyoSecp256K1UnCompressedPublicKey.id)
        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoSecp256k1Sha256WithEcdsaSignature.id)

        override fun newInstance(): XyoSigner {
            return XyoSha256WithSecp256K(null)
        }

        override fun newInstance(privateKey: XyoObject): XyoSigner {
            return XyoSha256WithSecp256K(privateKey)
        }

        override fun verifySign(signature: XyoObject, byteArray: ByteArray, publicKey: XyoObject): Deferred<Boolean> = GlobalScope.async {
            val signer = ECDSASigner()

            val ecDomainParameters = ECDomainParameters(ecCurve.getCurve(), ecCurve.getG(), ecCurve.getN())
            signer.init(false, ECPublicKeyParameters(ecCurve.getCurve().createPoint((publicKey as XyoUncompressedEcPublicKey).x, publicKey.y), ecDomainParameters))

            val data = XyoSha256.createHash(byteArray).await().objectInBytes

            if (signature is XyoSecp256k1Sha256WithEcdsaSignature) {
                val r = signature.r
                val s = signature.s

                return@async signer.verifySignature(data, r, s)
            }

            return@async false
        }
    }
}