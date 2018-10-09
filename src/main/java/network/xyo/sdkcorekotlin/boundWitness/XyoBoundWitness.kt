package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigner

/**
 * A Xyo Bound Witness Object
 *
 * @major 0x02
 * @minor 0x01
 */
abstract class XyoBoundWitness : XyoObject() {
    /**
     * All of the public keys in the bound witness.
     */
    abstract val publicKeys: Array<XyoKeySet>

    /**
     * All of the payloads in the bound witness.
     */
    abstract val payloads: Array<XyoPayload>

    /**
     * All of the signatures in the bound witness.
     */
    abstract val signatures: Array<XyoSignatureSet>

    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = 4

    /**
     * If the bound witness is completed or not.
     */
    val completed: Boolean
        get() {
            if (publicKeys.size == signatures.size && publicKeys.isNotEmpty()) {
                return true
            }
            return false
        }

    override val objectInBytes: ByteArray
        get() = makeBoundWitness()

    /**
     * Gets the hash of the bound witness.
     *
     * @param hashCreator A hash provider to create the hash with.
     * @return A deferred XyoHash
     */
    fun getHash(hashCreator: XyoHash.XyoHashProvider) = GlobalScope.async {
        val dataToHash = getSigningData()
        return@async hashCreator.createHash(dataToHash).await()
    }

    /**
     * Creates a signature of the bound witness.
     *
     * @param signer A signer to sign with.
     * @return A deferred XyoObject (signature).
     */
    fun signCurrent(signer: XyoSigner) = GlobalScope.async {
        val dataToSign = getSigningData()
        return@async signer.signData(dataToSign).await()
    }

    /**
     * Removes the unsigned payload from the bound witness.
     */
    fun removeAllUnsigned() {
        for (payload in payloads) {
            payload.unsignedPayload.array = arrayOf()
            payload.unsignedPayload.updateObjectCache()
            payload.updateObjectCache()
            updateObjectCache()
        }
    }

    fun getSigningData(): ByteArray {
        val setter = XyoByteArraySetter(payloads.size + 1)
        val makePublicKeysUntyped = makePublicKeys().untyped

        setter.add(makePublicKeysUntyped, 0)

        for (i in 0 until payloads.size) {
            val payload = payloads[i]
            setter.add(payload.signedPayload.untyped, i + 1)
        }

        return setter.merge()
    }

    private fun makeBoundWitness(): ByteArray {
        val setter = XyoByteArraySetter(3)
        val makePublicKeysUntyped = makePublicKeys().untyped
        val makePayloadsUntyped = makePayloads().untyped
        val makeSignaturesUntyped = makeSignatures().untyped

        setter.add(makePublicKeysUntyped, 0)
        setter.add(makePayloadsUntyped, 1)
        setter.add(makeSignaturesUntyped, 2)
        return setter.merge()

    }

    private fun makePublicKeys(): XyoSingleTypeArrayShort {
        return XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, Array(publicKeys.size, { i -> publicKeys[i] as XyoObject }))
    }

    private fun makePayloads(): XyoSingleTypeArrayInt {
        return XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, Array(payloads.size, { i -> payloads[i] as XyoObject }))
    }

    private fun makeSignatures(): XyoSingleTypeArrayShort {
        return XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, Array(signatures.size, { i -> signatures[i] as XyoObject }))
    }


    companion object : XyoObjectProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x01
        override val sizeOfBytesToGetSize: Int? = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            val shortArrayReadSize = XyoSingleTypeArrayShort.sizeOfBytesToGetSize ?: 0
            val intArrayReadSize = XyoSingleTypeArrayInt.sizeOfBytesToGetSize ?: 0

            return unpackIntoEncodedArrays(byteArray, shortArrayReadSize, intArrayReadSize)

        }

        private fun unpackIntoEncodedArrays(byteArray: ByteArray, shortArrayReadSize: Int, intArrayReadSize: Int): XyoObject {
            val byteReader = XyoByteArrayReader(byteArray)

            val keySetArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(4, shortArrayReadSize))
            val keySets = getKeySetsArray(byteReader.read(4, keySetArraySize))

            val payloadArraySize = XyoSingleTypeArrayInt.readSize(byteReader.read(keySetArraySize + 4, intArrayReadSize))
            val payloads = getPayloadsArray(byteReader.read(keySetArraySize + 4, payloadArraySize))

            val signatureArraySize = XyoSingleTypeArrayShort.readSize(byteReader.read(keySetArraySize + payloadArraySize + 4, shortArrayReadSize))
            val signatures = getSignatureArray(byteReader.read(keySetArraySize + payloadArraySize + 4, signatureArraySize))

            return unpackFromArrays(keySets, payloads, signatures)
        }


        private fun getKeySetsArray(bytes: ByteArray): Array<XyoKeySet> {
            val keySetArray = XyoSingleTypeArrayShort.createFromPacked(bytes) as XyoSingleTypeArrayShort
            return Array(keySetArray.size, { i -> keySetArray.array[i] as XyoKeySet })
        }

        private fun getPayloadsArray(bytes: ByteArray): Array<XyoPayload> {
            val payloadArray = XyoSingleTypeArrayInt.createFromPacked(bytes) as XyoSingleTypeArrayInt
            return Array(payloadArray.size, { i -> payloadArray.array[i] as XyoPayload })
        }

        private fun getSignatureArray(bytes: ByteArray): Array<XyoSignatureSet> {
            val signatureArray = XyoSingleTypeArrayShort.createFromPacked(bytes) as XyoSingleTypeArrayShort
            return Array(signatureArray.size, { i -> signatureArray.array[i] as XyoSignatureSet })
        }

        private fun unpackFromArrays(keysets: Array<XyoKeySet>, payloads: Array<XyoPayload>, signatures: Array<XyoSignatureSet>): XyoObject {
            return object : XyoBoundWitness() {
                override val payloads: Array<XyoPayload> = payloads
                override val publicKeys: Array<XyoKeySet> = keysets
                override val signatures: Array<XyoSignatureSet> = signatures
            }
        }

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }

        /**
         * Will verify a single bound witness.
         *
         * @param boundWitness The bound witness to verify
         * @return If the bound witness was successful and null if does not have the capability to
         * verify (cant find verify method)
         */
        fun verify (boundWitness: XyoBoundWitness) : Deferred<Boolean?> = async {
            if (!boundWitness.completed) {
                return@async false
            }

            val numberOfParties = getNumberOfParties(boundWitness)

            if (numberOfParties != null) {
                val signingData  = boundWitness.getSigningData()

                for (partyNum in 0 until numberOfParties) {
                    val keys = boundWitness.publicKeys[partyNum].array
                    val signatures = boundWitness.signatures[partyNum].array

                    if (keys.size != signatures.size) {
                        return@async false
                    }

                    for (keyNum in 0 until keys.size) {
                        val key = keys[keyNum]
                        val signature = signatures[keyNum]
                        val verify = XyoSigner.verify(key, signature, signingData).await()
                        if (XyoSigner.verify(key, signature, signingData).await() != true) {
                            return@async verify
                        }
                    }
                }

                return@async true
            }

            return@async false
        }

        /**
         * Will get the number of parties in a bound witness.
         *
         * @param boundWitness The boundWitness to check
         * @return The number of parties, if null, there is a inconsistent amount of parties.
         */
        fun getNumberOfParties (boundWitness: XyoBoundWitness) : Int? {
            val keySetNumber = boundWitness.publicKeys.size
            val payloadNumber = boundWitness.payloads.size
            val signatureNumber = boundWitness.signatures.size

            if (keySetNumber == payloadNumber &&  keySetNumber == signatureNumber) {
                return keySetNumber
            }

            return null
        }
    }
}
