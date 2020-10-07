package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.INDEX
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.NEXT_PUBLIC_KEY
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.nio.ByteBuffer


@ExperimentalStdlibApi
open class XyoOriginChainStateManager (val repo: XyoOriginChainStateRepository) {
    private var waitingSigners = ArrayList<XyoSigner>()
    var nextPublicKey : XyoObjectStructure? = null

    val statics: Array<XyoObjectStructure>
        get() = repo.getStaticHeuristics()

    val index : XyoObjectStructure
        get() = repo.getIndex() ?: XyoObjectStructure.newInstance(INDEX, ByteBuffer.allocate(4).putInt(0).array())

    val previousHash : XyoObjectStructure?
        get() = repo.getPreviousHash()

    val signers: Array<XyoSigner>
        get() = repo.getSigners()

    fun removeOldestSigner () {
        repo.removeOldestSigner()
    }

    fun addSigner (signer : XyoSigner) {
        val indexAsNumber = ByteBuffer.wrap(index.valueCopy).int

        if (indexAsNumber == 0) {
            repo.putSigner(signer)
            return
        }

        waitingSigners.add(signer)
        nextPublicKey = XyoObjectStructure.newInstance(NEXT_PUBLIC_KEY, signer.publicKey.bytesCopy)
    }

    fun newOriginBlock (hash : XyoHash) {
        val previousHash = XyoIterableStructure.createTypedIterableObject(XyoSchemas.PREVIOUS_HASH, arrayOf(hash))

        nextPublicKey = null
        addWaitingSigner()
        repo.putPreviousHash(previousHash)
        incrementIndex()
        repo.onBoundWitness()
    }

    private fun incrementIndex() {
        val indexAsNumber = ByteBuffer.wrap(index.valueCopy).int
        val nextIndex = XyoObjectStructure.newInstance(
                INDEX,
                ByteBuffer.allocate(4).putInt(indexAsNumber + 1).array()
        )
        repo.putIndex(nextIndex)
    }

    private fun addWaitingSigner () {
        if (waitingSigners.size > 0) {
            repo.putSigner(waitingSigners[0])
            waitingSigners.removeAt(0)
        }
    }
}