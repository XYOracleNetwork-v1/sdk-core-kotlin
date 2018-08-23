package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha1WithEcdsaSignature
import java.security.Signature

class XyoSha1WithSecp256K : XyoEcSecp256K() {
    override fun signData(byteArray: ByteArray): XyoObject {
        return XyoSha1WithEcdsaSignature(signatureInstance.run {
            initSign(keyPair.private)
            update(byteArray)
            sign()
        })
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA1withECDSA")

        override fun newInstance(): XyoSigningObject {
            return XyoSha256WithSecp256K()
        }

        override val key: Byte
            get() = 0x02
    }
}