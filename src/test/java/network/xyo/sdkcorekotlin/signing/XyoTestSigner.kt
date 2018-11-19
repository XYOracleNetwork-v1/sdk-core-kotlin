package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi

class XyoTestSigner : XyoSigner() {
    override val publicKey: XyoObject
        get() = XyoTestPlaceholder()

    override fun signData(byteArray: ByteArray) = GlobalScope.async {
        return@async XyoTestPlaceholder() as XyoObject
    }

    override val privateKey: XyoObject
        get() = XyoRssi(-22)
}
