package network.xyo.sdkcorekotlin.persist.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.persist.XyoStorageProvider
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import java.nio.ByteBuffer

class XyoStorageOriginStateRepository (private val store: XyoStorageProvider) : XyoOriginChainStateRepository {
    private var signersCache = ArrayList<XyoSigner>()
    private var staticsCache = ArrayList<XyoBuff>()
    private var indexCache: XyoBuff? = null
    private var previousHashCache: XyoBuff? = null
    private var lastBlockTimeCache: Long? = null

    override fun getIndex(): XyoBuff? {
        return indexCache
    }

    override fun putIndex(index: XyoBuff) {
        indexCache = index
    }

    override fun getPreviousHash(): XyoBuff? {
        return previousHashCache
    }

    override fun putPreviousHash(hash: XyoBuff) {
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

    override fun getStatics(): Array<XyoBuff> {
        return staticsCache.toTypedArray()
    }

    override fun setStaticts (statics: Array<XyoBuff>) {
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
            val encodedTime = XyoBuff.newInstance(XyoSchemas.BLOB, bytesLong)
            store.write(ORIGIN_LAST_TIME, encodedTime.bytesCopy).await()
        }


        val encodedStatics = XyoIterableObject.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, staticsCache.toTypedArray())
        store.write(ORIGIN_STATICS_KEY, encodedStatics.bytesCopy).await()
    }

    fun restore () = GlobalScope.async {
        val encodedIndex = store.read(ORIGIN_STATE_INDEX_KEY).await()
        val encodedHash = store.read(ORIGIN_HASH_INDEX_KEY).await()
        val encodedStaticts = store.read(ORIGIN_STATICS_KEY).await()
        val encodedLastTime = store.read(ORIGIN_LAST_TIME).await()

        if (encodedIndex != null) {
            indexCache = XyoBuff.wrap(encodedIndex)
        }

        if (encodedHash != null) {
            previousHashCache = XyoBuff.wrap(encodedHash)
        }

        if (encodedStaticts != null) {
            val statics = ArrayList<XyoBuff>()
            val it = object : XyoIterableObject() {
                override val allowedOffset: Int = 0
                override var item: ByteArray = encodedStaticts
            }.iterator

            for (item in it) {
                statics.add(item)
            }

            staticsCache = statics
        }

        if (encodedLastTime != null) {
            val buff = XyoBuff.wrap(encodedLastTime).valueCopy
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