package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.XyoObject

class XyoTestSigner : XyoSigner() {
    override val publicKey: XyoObject
        get() = XyoTestPlaceholder()

    override fun signData(byteArray: ByteArray) = GlobalScope.async {
        return@async XyoTestPlaceholder() as XyoObject
    }

    override val privateKey: ByteArray
        get() = byteArrayOf()
}
