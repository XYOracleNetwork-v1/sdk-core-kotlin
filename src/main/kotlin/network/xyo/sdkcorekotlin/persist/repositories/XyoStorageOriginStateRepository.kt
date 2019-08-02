package network.xyo.sdkcorekotlin.persist.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.persist.XyoKeyValueStore
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.nio.ByteBuffer

class XyoStorageOriginStateRepository (private val store: XyoKeyValueStore) : XyoOriginChainStateRepository {
    private var signersCache = ArrayList<XyoSigner>()
    private var staticsCache = ArrayList<XyoObjectStructure>()
    private var indexCache: XyoObjectStructure? = null
    private var previousHashCache: XyoObjectStructure? = null
    private var lastBlockTimeCache: Long? = null

    override fun getIndex(): XyoObjectStructure? {
        return indexCache
    }

    override fun putIndex(index: XyoObjectStructure) {
        indexCache = index
    }

    override fun getPreviousHash(): XyoObjectStructure? {
        return previousHashCache
    }

    override fun putPreviousHash(hash: XyoObjectStructure) {
        previousHashCache = hash
    }

    override fun getSigners(): Array<XyoSigner> {
        return signersCache.toTypedArray()
    }

    override fun removeOldestSigner() {
        if (signersCache.isNotEmpty()) {
            signersCache.removeAt(0)
        }
    }

    override fun putSigner(signer: XyoSigner) {
        signersCache.add(signer)
    }

    override fun getStaticHeuristics(): Array<XyoObjectStructure> {
        return staticsCache.toTypedArray()
    }

    override fun setStaticHeuristics (statics: Array<XyoObjectStructure>) {
        staticsCache = ArrayList(statics.asList())
    }

    override fun onBoundWitness () {
        lastBlockTimeCache = System.currentTimeMillis()
    }

    override fun getLastBoundWitnessTime () : Long? {
        return lastBlockTimeCache
    }

    override fun commit(): Deferred<Unit> = GlobalScope.async {
        val index = indexCache
        val previousHash = previousHashCache
        val originTime = lastBlockTimeCache

        if (index != null) {
            store.write(ORIGIN_STATE_INDEX_KEY, index.bytesCopy).await()
        }

        if (previousHash != null) {
            store.write(ORIGIN_HASH_INDEX_KEY, previousHash.bytesCopy).await()
        }

        if (originTime != null) {
            val bytesLong = ByteBuffer.allocate(8).putLong(originTime).array()
            val encodedTime = XyoObjectStructure.newInstance(XyoSchemas.BLOB, bytesLong)
            store.write(ORIGIN_LAST_TIME, encodedTime.bytesCopy).await()
        }


        val encodedStatics = XyoIterableStructure.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, staticsCache.toTypedArray())
        store.write(ORIGIN_STATICS_KEY, encodedStatics.bytesCopy).await()
    }

    fun restore (signers: ArrayList<XyoSigner>) = GlobalScope.async {
        signersCache = signers
        val encodedIndex = store.read(ORIGIN_STATE_INDEX_KEY).await()
        val encodedHash = store.read(ORIGIN_HASH_INDEX_KEY).await()
        val encodedStaticts = store.read(ORIGIN_STATICS_KEY).await()
        val encodedLastTime = store.read(ORIGIN_LAST_TIME).await()

        if (encodedIndex != null) {
            indexCache = XyoObjectStructure.wrap(encodedIndex)
        }

        if (encodedHash != null) {
            previousHashCache = XyoObjectStructure.wrap(encodedHash)
        }

        if (encodedStaticts != null) {
            val statics = ArrayList<XyoObjectStructure>()
            val it =  XyoIterableStructure(encodedStaticts, 0).iterator

            for (item in it) {
                statics.add(item)
            }

            staticsCache = statics
        }

        if (encodedLastTime != null) {
            val buff = XyoObjectStructure.wrap(encodedLastTime).valueCopy
            lastBlockTimeCache = ByteBuffer.wrap(buff).long
        }
    }

    companion object {
        private val ORIGIN_STATE_INDEX_KEY = "ORIGIN_STATE_INDEX_KEY".toByteArray()
        private val ORIGIN_HASH_INDEX_KEY = "ORIGIN_HASH_INDEX_KEY".toByteArray()
        private val ORIGIN_STATICS_KEY = "ORIGIN_STATICS_KEY".toByteArray()
        private val ORIGIN_LAST_TIME = "ORIGIN_LAST_TIME".toByteArray()
    }
}