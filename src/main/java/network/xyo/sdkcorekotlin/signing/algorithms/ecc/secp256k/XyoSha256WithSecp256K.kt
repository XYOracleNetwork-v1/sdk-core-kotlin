package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import jdk.nashorn.internal.objects.Global
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Signature
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import java.security.PublicKey
import java.security.interfaces.ECPrivateKey


/**
 * A Xyo Signer using EC with the Secp256K1 curve with SHA256.
 */
class XyoSha256WithSecp256K (privateKey : ECPrivateKey?) : XyoEcSecp256K(privateKey) {

    @ExperimentalUnsignedTypes
    override fun signData(byteArray: ByteArray): Deferred<ByteArray> {
        return GlobalScope.async {
            signatureInstance.initSign(keyPair.private)
            signatureInstance.update(byteArray)
            signatureInstance.sign()

            val pam = ECPrivateKeyParameters((keyPair.private as XyoEcPrivateKey).s, ecDomainParameters)

            val signer = ECDSASigner()
            signer.init(true, pam)
            val sig = signer.generateSignature(XyoSha256.createHash(byteArray).await().hash)

            return@async XyoEcdsaSignature(sig[0], sig[1]).self
        }
    }

    companion object : XyoSigningObjectCreatorVerify() {

        override val signatureInstance: Signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider())
        override val key: Byte = 0x01

        @ExperimentalUnsignedTypes
        override val supportedKeys: Array<ByteArray> = arrayOf(XyoSchemas.EC_PUBLIC_KEY.header)

        @ExperimentalUnsignedTypes
        override val supportedSignatures: Array<ByteArray> = arrayOf(XyoSchemas.EC_PRIVATE_KEY.header)

        override fun newInstance(): XyoSigner {
            return XyoSha256WithSecp256K(null)
        }

        @ExperimentalUnsignedTypes
        override fun newInstance(privateKey: ByteArray): XyoSigner {
            return XyoSha256WithSecp256K(XyoEcPrivateKey.getInstance(privateKey, getSpec()))
        }

        @ExperimentalUnsignedTypes
        override fun verifySign(signature: ByteArray, byteArray: ByteArray, publicKey: PublicKey): Deferred<Boolean> = GlobalScope.async {
            val signer = ECDSASigner()

            val ecDomainParameters = ECDomainParameters(ecCurve.curve, ecCurve.g, ecCurve.n)
            signer.init(false, ECPublicKeyParameters(ecCurve.curve.createPoint((publicKey as XyoUncompressedEcPublicKey).x, publicKey.y), ecDomainParameters))

            val data = XyoSha256.createHash(byteArray).await().hash

            val ecSig = XyoEcdsaSignature.getInstance(signature)

            if (ecSig is XyoEcdsaSignature) {
                val r = ecSig.r
                val s = ecSig.s

                return@async signer.verifySignature(data, r, s)
            }

            return@async false
        }
    }
}