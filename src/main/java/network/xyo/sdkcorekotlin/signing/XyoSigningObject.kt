package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject

abstract class XyoSigningObject {
    abstract val publicKey : XyoResult<XyoObject>
    abstract fun signData (byteArray: ByteArray) : Deferred<XyoResult<XyoObject>>

    abstract class XYOSigningCreator {
        abstract fun newInstance () : XyoResult<XyoSigningObject>
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
        private val signingCreators = HashMap<Byte, XYOSigningCreator>()

        fun getCreator (byte : Byte) : XYOSigningCreator? {
            return signingCreators[byte]
        }
    }
}