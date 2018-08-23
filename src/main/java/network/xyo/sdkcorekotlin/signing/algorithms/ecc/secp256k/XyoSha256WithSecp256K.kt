package network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObjectCreatorVerify
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha256WithEcdsaSignature
import java.security.Signature


class XyoSha256WithSecp256K : XyoEcSecp256K() {
    override fun signData(byteArray: ByteArray): XyoObject {
         return XyoSha256WithEcdsaSignature(signatureInstance.run {
            initSign(keyPair.private)
            update(byteArray)
            sign()
        })
    }

    companion object : XyoSigningObjectCreatorVerify() {
        override val signatureInstance: Signature = Signature.getInstance("SHA256withECDSA")

        override fun newInstance(): XyoSigningObject {
            return XyoSha256WithSecp256K()
        }

        override val key: Byte
            get() = 0x01
    }
}