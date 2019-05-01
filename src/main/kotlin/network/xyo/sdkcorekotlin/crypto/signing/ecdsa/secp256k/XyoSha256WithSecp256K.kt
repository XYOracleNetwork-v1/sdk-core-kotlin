package network.xyo.sdkcorekotlin.crypto.signing.ecdsa.secp256k

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.XyoEcdsaSignature
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.XyoUncompressedEcPublicKey
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.spec.ECParameterSpec
import java.security.MessageDigest
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

    companion object : XyoSigner.XyoSignerProvider() {
        private val signatureInstance: Signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider())
        override val key: Byte = 0x01
        override val supportedKeys: Array<Byte> = arrayOf(XyoSchemas.EC_PUBLIC_KEY.id)
        override val supportedSignatures: Array<Byte> = arrayOf(XyoSchemas.EC_SIGNATURE.id)

        override fun newInstance(): XyoSigner {
            return XyoSha256WithSecp256K(null)
        }

        override fun newInstance(privateKey: ByteArray): XyoSigner {
            return XyoSha256WithSecp256K(XyoEcPrivateKey.getInstance(privateKey, XyoEcSecp256K1.ecSpec))
        }

        override fun verifySign(signature: XyoBuff, byteArray: ByteArray, publicKey: XyoBuff): Deferred<Boolean> = GlobalScope.async {
            try {
                val signer = ECDSASigner()
                val uncompressedKey = object :XyoUncompressedEcPublicKey() {
                    override val ecSpec: ECParameterSpec = XyoEcSecp256K1.ecSpec
                    override val allowedOffset: Int = 0
                    override var item: ByteArray = publicKey.bytesCopy
                }

                val ecDomainParameters = ECDomainParameters(ecCurve.curve, ecCurve.g, ecCurve.n)
                signer.init(false, ECPublicKeyParameters(ecCurve.curve.createPoint(uncompressedKey.x, uncompressedKey.y), ecDomainParameters))


                val ecSig = XyoEcdsaSignature.getInstance(signature.bytesCopy)

                val r = ecSig.r
                val s = ecSig.s

                return@async signer.verifySignature(hashData(byteArray), r, s)

                // if point is not on curve
            } catch (e : IllegalArgumentException) {
                return@async false
            }
        }

        private fun hashData (byteArray: ByteArray) : ByteArray {
            return MessageDigest.getInstance("SHA-256").digest(byteArray)
        }
    }
}