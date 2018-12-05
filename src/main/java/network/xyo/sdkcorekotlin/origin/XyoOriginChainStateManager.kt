package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.INDEX
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.NEXT_PUBLIC_KEY
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.PREVIOUS_HASH
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
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
    private var latestHash : XyoBuff? = null

    constructor(indexOffset: Int, signers : Array<XyoSigner>, previousHash: XyoBuff): this(indexOffset) {
        latestHash = previousHash
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
    val allPublicKeys = ArrayList<XyoBuff>()

    override var nextPublicKey : XyoBuff? = null

    override val index : XyoBuff
        get() = XyoBuff.newInstance(INDEX, ByteBuffer.allocate(4).putInt(count + indexOffset).array())

    override val previousHash : XyoBuff?
        get() {
            val latestHashValue = latestHash
            if (latestHashValue != null) {
                return XyoBuff.newInstance(PREVIOUS_HASH, latestHashValue.bytesCopy)
            }
            return null
        }


    override fun getSigners () : Array<XyoSigner> {
        return currentSigners.toTypedArray()
    }

    override fun addSigner (signer : XyoSigner) {
        if (ByteBuffer.wrap(index.valueCopy).int == 0) {
            currentSigners.add(signer)
            return
        }

        nextPublicKey = XyoBuff.newInstance(NEXT_PUBLIC_KEY, signer.publicKey.bytesCopy)
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