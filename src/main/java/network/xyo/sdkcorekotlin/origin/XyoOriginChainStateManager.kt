package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.signing.XyoSigner


/**
 * An implementation of the XyoOriginStateRepository.
 *
 * @param indexOffset This value is used to create a state manager where the index does not doBoundWitness
 * at 0. This is used when re-starting a origin chain.
 */
open class XyoOriginChainStateManager (private val indexOffset : Int) : XyoOriginStateRepository {
    private var currentSigners = ArrayList<XyoSigner>()
    private var waitingSigners = ArrayList<XyoSigner>()
    private var latestHash : XyoHash? = null

    constructor(indexOffset: Int, signers : Array<XyoSigner>, previousHash: XyoPreviousHash): this(indexOffset) {
        latestHash = previousHash.hash
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
    val allPublicKeys = ArrayList<XyoObject>()

    override var nextPublicKey : XyoNextPublicKey? = null

    override val index : XyoIndex
        get() = XyoIndex(count + indexOffset)


    override val previousHash : XyoPreviousHash?
        get() {
            val latestHashValue = latestHash
            if (latestHashValue != null) {
                return XyoPreviousHash(latestHashValue)
            }
            return null
        }


    override fun getSigners () : Array<XyoSigner> {
        return currentSigners.toTypedArray()
    }

    override fun addSigner (signer : XyoSigner) {
        nextPublicKey = XyoNextPublicKey(signer.publicKey)
        waitingSigners.add(signer)
        allPublicKeys.add(signer.publicKey)
    }

    override fun removeOldestSigner () {
        currentSigners.removeAt(0)
    }

    override fun newOriginBlock (hash : XyoHash) {
        nextPublicKey = null
        allHashes.add(hash)
        latestHash = hash
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