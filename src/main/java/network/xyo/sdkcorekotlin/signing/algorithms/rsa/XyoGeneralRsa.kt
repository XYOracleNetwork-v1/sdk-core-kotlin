package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import java.security.*
import java.security.interfaces.RSAPublicKey


abstract class XyoGeneralRsa(keySize : Int) : XyoSigningObject() {
    private val mKeyGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    private val mKeySize : Int = keySize
    abstract val signature : Signature

    override val publicKey: XyoObject
        get() = keyPair.public as XyoRsaPublicKey

    open val keyPair: KeyPair = generateKeyPair()

    private fun generateKeyPair(): KeyPair {
        mKeyGenerator.initialize(mKeySize)
        val standardKeyPair = mKeyGenerator.genKeyPair()
        val publicKey = standardKeyPair.public as? RSAPublicKey

        if (publicKey != null) {
            return KeyPair(XyoRsaPublicKey(publicKey.modulus), standardKeyPair.private)
        }

        throw Exception()
    }
}