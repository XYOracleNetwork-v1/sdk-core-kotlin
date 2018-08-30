package network.xyo.sdkcorekotlin.signing.algorithms.rsa

import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigner
import java.security.*
import java.security.interfaces.RSAPublicKey


abstract class XyoGeneralRsa(keySize : Int) : XyoSigner() {
    private val mKeyGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    private val mKeySize : Int = keySize
    abstract val signature : Signature

    override val publicKey: XyoResult<XyoObject>
        get() {
            val rsaKeyPair = keyPair.public as? XyoRsaPublicKey
            if (rsaKeyPair != null) {
                return XyoResult(rsaKeyPair)
            }
            return XyoResult(XyoError(""))
        }

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