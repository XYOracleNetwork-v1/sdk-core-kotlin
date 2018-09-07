package network.xyo.sdkcorekotlin.signing

import kotlinx.coroutines.experimental.Deferred
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.XyoObject

/**
 * Performs public key cryptographic operations. A XyoSigner is obtained from a
 * XyoSignerProvider with newInstance(). If a compatible private key is provided, the
 * XyoCryptoSigner will create its keypair using this private key else it will create a random
 * keypair. This privateKey is obtained through a XyoSigner with getPrivate().
 */
abstract class XyoSigner {
    /**
     * The public key of the XyoSigner.
     */
    abstract val publicKey : XyoResult<XyoObject>

    /**
     * Cryptographically signs a given ByteArray so that it can verified with verify().
     *
     * @param byteArray The data to cryptographically sign using the private key of the
     * XyoSigner.
     * @return A deferred cryptographic signature of the data field, that was
     * created with the private key, in form of a XyoResult<XyoObject>
     */
    abstract fun signData (byteArray: ByteArray) : Deferred<XyoResult<XyoObject>>

    /**
     * Gives access to a XyoSigner that can preform public key cryptographic functions.
     */
    abstract class XyoSignerProvider {
        /**
         * Provides a new instance of a XyoSigner for the given algorithm.
         */
        abstract fun newInstance () : XyoResult<XyoSigner>

        /**
         * Cryptographically verify a signature given data, a signature, and a public
         * key that the XyoSigner supports.
         *
         * @param signature The signature that was created using the cryptographic function that
         * the XyoSigner supports.
         * @param byteArray The data that was signed using the cryptographic function that the
         * XyoSigner supports.
         * @param publicKey The public key of the party that signed the data with the the
         * cryptographic function that the XyoSigner supports.
         * @return If the signature is valid, the deferred XyoResult<Boolean> will be true, if it
         * is invalid the deferred XyoResult<Boolean> will be false.
         */
        abstract fun verifySign (signature: XyoObject,
                                 byteArray: ByteArray,
                                 publicKey : XyoObject) : Deferred<XyoResult<Boolean>>

        /**
         * The key to identify the signer provider by so it can be added to a mapping.
         */
        abstract val key : Byte

        /**
         * Adds the signer provider to the mapping.
         */
        fun enable () {
            signingCreators[key] = this
        }

        /**
         * Removes the signer provider to the mapping.
         */
        fun disable () {
            signingCreators.remove(key)
        }
    }

    companion object {
        private val signingCreators = HashMap<Byte, XyoSignerProvider>()

        /**
         * Gets a signer provider by its key.
         *
         * @param byte The key of the signer provider.
         * @return A signer provider if it exists.
         */
        fun getCreator (byte : Byte) : XyoSignerProvider? {
            return signingCreators[byte]
        }
    }
}