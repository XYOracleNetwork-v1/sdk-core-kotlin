package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject

abstract class XyoSigner {
    abstract val publicKey : XyoResult<XyoObject>
    abstract fun signData (byteArray: ByteArray) : Deferred<XyoResult<XyoObject>>

    abstract class XyoSignerProvider {
        abstract fun newInstance () : XyoResult<XyoSigner>
        abstract fun verifySign (signature: XyoObject, byteArray: ByteArray, publicKey : XyoObject) : Deferred<XyoResult<Boolean>>
        abstract val key : Byte

        fun enable () {
            signingCreators[key] = this
        }

        fun disable () {
            signingCreators.remove(key)
        }
    }

    companion object {
        private val signingCreators = HashMap<Byte, XyoSignerProvider>()

        fun getCreator (byte : Byte) : XyoSignerProvider? {
            return signingCreators[byte]
        }
    }
}