package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.INDEX
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import java.nio.ByteBuffer


/**
 * An implementation of the XyoOriginStateRepository.
 *
 * @param indexOffset This value is used to create a state manager where the index does not doBoundWitness
 * at 0. This is used when re-starting a origin chain.
 */
open class XyoOriginChainStateManager (private val indexOffset : Int) : XyoOriginStateRepository {
    private var currentSigners = ArrayList<XyoSigner>()
    private var waitingSigners = ArrayList<XyoSigner>()
    private var latestHash : ByteArray? = null

    @ExperimentalUnsignedTypes
    constructor(indexOffset: Int, signers : Array<XyoSigner>, previousHash: XyoPreviousHash): this(indexOffset) {
        latestHash = previousHash.previousHash
        currentSigners = ArrayList(signers.toList())
    }

    /**
     * The total number of elements since creation, NOT including the indexOffset.
     * */
    var count = 0

    /**
     * All of the hashes in the origin chain.
     */
    val allHashes = ArrayList<XyoHash>()

    /**
     * All of the public keys used in the origin chain.
     */
    val allPublicKeys = ArrayList<ByteArray>()

    override var nextPublicKey : XyoNextPublicKey? = null

    @ExperimentalUnsignedTypes
    override val index : ByteArray
        get() = XyoObjectCreator.createObject(INDEX, ByteBuffer.allocate(4).putInt(count + indexOffset).array())


    @ExperimentalUnsignedTypes
    override val previousHash : XyoPreviousHash?
        get() {
            val latestHashValue = latestHash
            if (latestHashValue != null) {
                return XyoPreviousHash.createFromHash(latestHashValue)
            }
            return null
        }


    override fun getSigners () : Array<XyoSigner> {
        return currentSigners.toTypedArray()
    }

    @ExperimentalUnsignedTypes
    override fun addSigner (signer : XyoSigner) {
        if (ByteBuffer.wrap(index).int == 0) {
            currentSigners.add(signer)
            return
        }

        nextPublicKey = XyoNextPublicKey.createFromHash(signer.publicKey.self)
        waitingSigners.add(signer)
        allPublicKeys.add(signer.publicKey.self)
    }

    override fun removeOldestSigner () {
        currentSigners.removeAt(0)
    }

    override fun newOriginBlock (hash : XyoHash) {
        nextPublicKey = null
        allHashes.add(hash)
        latestHash = hash.self
        count++
        addWaitingSigner()
    }

    private fun addWaitingSigner () {
        if (waitingSigners.size > 0) {
            currentSigners.add(waitingSigners.first())
            waitingSigners.removeAt(0)
        }
    }
}