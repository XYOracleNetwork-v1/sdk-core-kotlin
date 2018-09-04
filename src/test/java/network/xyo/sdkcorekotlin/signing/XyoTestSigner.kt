package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject

class XyoTestSigner : XyoSigner() {
    override val publicKey: XyoResult<XyoObject>
        get() = XyoResult(XyoTestPlaceholder())

    override fun signData(byteArray: ByteArray) = async {
        return@async XyoResult(XyoTestPlaceholder() as XyoObject)
    }
}
