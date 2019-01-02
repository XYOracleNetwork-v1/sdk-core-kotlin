package network.xyo.sdkcorekotlin.crypto.signing

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.security.PrivateKey
import java.security.PublicKey

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
    abstract val publicKey : XyoPublicKey

    /**
     * The private key of the XyoSigner, this can be used to restore signer state.
     */
    abstract val privateKey : XyoPrivateKey

    /**
     * Cryptographically signs a given ByteArray so that it can verified with verify().
     *
     * @param byteArray The data to cryptographically sign using the private key of the
     * XyoSigner.
     * @return A deferred cryptographic signature of the data field, that was
     * created with the private key, in form of a XyoObject
     */
    abstract fun signData (byteArray: ByteArray) : Deferred<XyoBuff>

    /**
     * Gives access to a XyoSigner that can preform public key cryptographic functions.
     */
    abstract class XyoSignerProvider {
        /**
         * Provides a new instance of a XyoSigner for the given algorithm and generates a keypair.
         */
        abstract fun newInstance () : XyoSigner

        /**
         * Provides a new instance of a XyoSigner for the given algorithm and generates a keypair with the given
         * private key.
         */
        abstract fun newInstance (privateKey : ByteArray) : XyoSigner

        /**
         * Cryptographically verify a signature given data, a signature, and a public
         * key that the XyoSigner supports.
         *
         * @param signature The signature that was created using the cryptographic function that the XyoSigner
         * supports.
         * @param byteArray The data that was signed using the cryptographic function that the XyoSigner supports.
         * @param publicKey The public key of the party that signed the data with the the cryptographic function that
         * the XyoSigner supports.
         * @return If the signature is valid, the deferred Boolean will be true, if it is invalid the deferred
         * <Boolean will be false.
         */
        abstract fun verifySign (signature: XyoBuff,
                                 byteArray: ByteArray,
                                 publicKey : XyoBuff) : Deferred<Boolean>

        /**
         * The key to identify the signer provider by so it can be added to a mapping.
         */
        abstract val key : Byte

        /**
         * The keys types the signer supports
         */
        abstract val supportedKeys : Array<Byte>

        /**
         * The signaturePacking types the signer supports
         */
        abstract val supportedSignatures : Array<Byte>

        /**
         * Adds the signer provider to the mapping.
         */
        fun enable () {
            signingCreators[key] = this

            for (key in supportedKeys) {
                for (sig in supportedSignatures) {
                    val map = verifiers[key] ?: HashMap()
                    map[sig] = this
                    verifiers[key] = map
                }
            }
        }


        /**
         * Removes the signer provider to the mapping.
         */
        fun disable () {
            signingCreators.remove(key)

            for (key in supportedKeys) {
                for (sig in supportedSignatures) {
                    verifiers[key] ?: return
                    verifiers[key]?.remove(sig)

                    if (verifiers[key]?.size == 0) {
                        verifiers.remove(key)
                    }
                }
            }
        }
    }

    companion object {
        /**
         * [major and minor of key (content hash code)][major and minor of sig (content hash code)]
         */
        private val verifiers = HashMap<Byte, HashMap<Byte, XyoSignerProvider>>()
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

        fun verify (publicKey: XyoBuff, signature: XyoBuff, data : ByteArray) : Deferred<Boolean?> = GlobalScope.async {
            val headerPublicKey = publicKey.schema.id
            val creator = verifiers[headerPublicKey]?.get(signature.schema.id)

            if (creator != null) {
                return@async creator.verifySign(signature, data, publicKey).await()
            }

            return@async null
        }
    }
}