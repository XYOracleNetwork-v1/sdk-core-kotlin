package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha1WithEcdsaSignature
import org.bouncycastle.asn1.DEREncodableVector
import org.bouncycastle.asn1.x9.ECNamedCurveTable
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jcajce.provider.asymmetric.X509
import sun.security.util.DerEncoder
import java.security.Signature
import java.security.cert.X509Certificate
import java.security.spec.RSAPublicKeySpec
import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom.getSeed
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util.getCurve
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.params.ECDomainParameters
import java.security.PrivateKey





/**
 * A Xyo Signer using EC with the Secp256K curve with SHA1.
 */
class XyoSha1WithSecp256K (privateKey : XyoObject?) : XyoEcSecp256K(privateKey) {
    override fun signData(byteArray: ByteArray): Deferred<XyoObject> {
        return GlobalScope.async {


            val ecCurve = ECNamedCurveTable.getByName("secp256k1")
            val ecDomainParameters = ECDomainParameters(ecCurve.curve, ecCurve.g, ecCurve.n)

            signatureInstance.initSign(keyPair.private)
            signatureInstance.update(byteArray)
            signatureInstance.sign()

            val pam = ECPrivateKeyParameters((keyPair.private as XyoEcPrivateKey).s, ecDomainParameters)

            val signer = ECDSASigner()
            signer.init(true, pam)
            val sig = signer.generateSignature(byteArray)

            return@async XyoSecp256k1Sha1WithEcdsaSignature(sig[0], sig[1])
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA1withECDSA")
        override val key: Byte = 0x02

        override val supportedKeys: Array<ByteArray> = arrayOf(XyoSecp256K1UnCompressedPublicKey.id)
        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoSecp256k1Sha1WithEcdsaSignature.id)

        override fun newInstance(): XyoSigner {
            return XyoSha256WithSecp256K(null)
        }

        override fun newInstance(privateKey: XyoObject): XyoSigner {
            return XyoSha256WithSecp256K(privateKey)
        }
    }
}