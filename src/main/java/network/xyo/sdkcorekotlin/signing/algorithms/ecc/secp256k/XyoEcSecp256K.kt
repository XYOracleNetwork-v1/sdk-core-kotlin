package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoGeneralEc
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.XyoEcPrivateKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import org.bouncycastle.jce.spec.ECKeySpec
import org.bouncycastle.jce.spec.ECParameterSpec
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import sun.jvm.hotspot.code.CompressedStream.H
import javax.swing.text.html.HTML.Attribute.N
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.tls.NamedCurve
import org.bouncycastle.jce.ECNamedCurveTable
import java.security.spec.*


/**
 * A base class for all EC operations using the Secp256K curve.
 */
abstract class XyoEcSecp256K (private val encodedPrivateKey: ByteArray?) : XyoGeneralEc() {
    /**
     * The generated public key.
     */
    val keyPair: KeyPair = generateKeyPair(encodedPrivateKey)

    override val publicKey: XyoObject
        get() = keyPair.public as XyoSecp256K1UnCompressedPublicKey

    override val privateKey: ByteArray
        get() = (keyPair.private as XyoEcPrivateKey).untyped

    private fun generateKeyPair(encodedPrivateKey: ByteArray?): KeyPair {
        if (encodedPrivateKey != null) {
            return generateKeyFromPrivate(XyoEcPrivateKey.createFromPacked(encodedPrivateKey) as XyoEcPrivateKey)
        }
        return generateNewKeyPair()
    }

    private fun generateNewKeyPair () : KeyPair {
        val keyGenerator : KeyPairGenerator = KeyPairGenerator.getInstance("EC")

        keyGenerator.initialize(XyoSecp256K1UnCompressedPublicKey.ecPramSpec)
        val generatedKeyPair = keyGenerator.genKeyPair()
        val ecPublic =  generatedKeyPair.public as ECPublicKey
        val ecPrivate = generatedKeyPair.private as ECPrivateKey

        return KeyPair(
                object : XyoSecp256K1UnCompressedPublicKey() {
                    override val x: BigInteger
                        get() = ecPublic.w.affineX

                    override val y: BigInteger
                        get() = ecPublic.w.affineY
                },
                XyoEcPrivateKey(ecPrivate.s, XyoSecp256K1UnCompressedPublicKey.ecPramSpec))
    }

    private fun generateKeyFromPrivate (privateKey: XyoEcPrivateKey) : KeyPair {
        val keyGenerator : KeyFactory = KeyFactory.getInstance("EC")
        val publicKey = keyGenerator.generatePublic(getSpecFromPrivateKey(privateKey)) as ECPublicKey

        return KeyPair(object : XyoSecp256K1UnCompressedPublicKey() {
            override val x: BigInteger
                get() = publicKey.w.affineX

            override val y: BigInteger
                get() = publicKey.w.affineY

            }, privateKey)
    }

    private fun getSpecFromPrivateKey (privateKey: XyoEcPrivateKey) : ECPublicKeySpec {
        val curve = ECNamedCurveTable.getParameterSpec("secp256k1")
        val domain = ECDomainParameters(curve.curve, curve.g, curve.n, curve.h)

        val q = domain.g.multiply(privateKey.s)
        val point = ECPoint(q.x.toBigInteger(), q.y.toBigInteger())

        return ECPublicKeySpec(point,  privateKey.params)
    }
}