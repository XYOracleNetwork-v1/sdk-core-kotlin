package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject

abstract class XyoSigningObject {
    abstract val publicKey : XyoObject
    abstract fun signData (byteArray: ByteArray) : XyoObject

    abstract class XYOSigningCreator {
        abstract fun newInstance () : XyoSigningObject
        abstract fun verifySign (signature: XyoObject, byteArray: ByteArray, publicKey : XyoObject) : Boolean?
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