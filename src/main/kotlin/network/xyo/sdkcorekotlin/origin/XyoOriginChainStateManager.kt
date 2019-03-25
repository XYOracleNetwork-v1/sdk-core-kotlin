package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.INDEX
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.NEXT_PUBLIC_KEY
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.PREVIOUS_HASH
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import java.nio.ByteBuffer



open class XyoOriginChainStateManager (val repo: XyoOriginChainStateRepository) {
    private var waitingSigners = ArrayList<XyoSigner>()
    var nextPublicKey : XyoBuff? = null

    val statics: Array<XyoBuff>
        get() = repo.getStaticts()

    val index : XyoBuff
        get() = repo.getIndex() ?: XyoBuff.newInstance(INDEX, ByteBuffer.allocate(4).putInt(0).array())

    val previousHash : XyoBuff?
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
        nextPublicKey = XyoBuff.newInstance(NEXT_PUBLIC_KEY, signer.publicKey.bytesCopy)
    }

    fun newOriginBlock (hash : XyoHash) {
        val previousHash = XyoIterableObject.createTypedIterableObject(XyoSchemas.PREVIOUS_HASH, arrayOf(hash))

        nextPublicKey = null
        addWaitingSigner()
        repo.putPreviousHash(previousHash)
        incrementIndex()
        repo.onBoundWitness()
    }

    private fun incrementIndex() {
        val indexAsNumber = ByteBuffer.wrap(index.valueCopy).int
        val nextIndex = XyoBuff.newInstance(
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