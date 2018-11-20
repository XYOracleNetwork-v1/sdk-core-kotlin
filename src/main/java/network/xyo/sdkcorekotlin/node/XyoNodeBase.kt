package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitnessSession
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.origin.XyoIndexableOriginBlockRepository
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.ARRAY_UNTYPED
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BRIDGE_BLOCK_SET
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.PAYLOAD
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectIterator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * A base class for all things creating an managing an origin chain (e.g. Sentinel, Bridge).
 *
 * @param storageProvider A place to store all origin blocks.
 * @param hashingProvider A hashing provider to use hashing utilises.
 */
abstract class XyoNodeBase (storageProvider : XyoStorageProviderInterface,
                            private val hashingProvider : XyoHash.XyoHashProvider) {

    private val boundWitnessOptions = ConcurrentHashMap<Int, XyoBoundWitnessOption>()
    private val heuristics = ConcurrentHashMap<String, XyoHeuristicGetter>()
    private val listeners = ConcurrentHashMap<String, XyoNodeListener>()

    @ExperimentalUnsignedTypes
    private var currentBoundWitnessSession : XyoZigZagBoundWitnessSession? = null

    /**
     * Gets the choice of a catalog from another party.
     *
     * @param catalog The catalog of the other party.
     * @return The choice to preform in the bound witness.
     */
    abstract fun getChoice (catalog : Int, strict : Boolean) : Int

    /**
     * All of the origin blocks that the node contains.
     */
    open val originBlocks : XyoIndexableOriginBlockRepository = XyoIndexableOriginBlockRepository(hashingProvider, storageProvider)

    /**
     * The current origin state of the origin node.
     */
    open var originState = XyoOriginChainStateManager(0)

    /**
     * Adds a heuristic to be used when creating bound witnesses.
     *
     * @param key The key for the heuristic.
     * @param heuristic The heuristic to use in  bound witnesses.
     */
    fun addHeuristic (key: String, heuristic : XyoHeuristicGetter) {
        heuristics[key] = heuristic
    }

    /**
     * Removes a heuristic from the current heuristic pool.
     *
     * @param key The key of the heuristic to use.
     */
    fun removeHeuristic (key: String) {
        heuristics.remove(key)
    }

    /**
     * Adds a Node Listener to listen for bound witness creations.
     *
     * @param key The key of the listener.
     * @param listener The XyoNodeListener to call back to.
     */
    fun addListener (key : String, listener : XyoNodeListener) {
        listeners[key] = listener
    }

    /**
     * Removes a listener from the current listener pool.
     *
     * @param key The key of the listener to remove.
     */
    fun removeListener (key : String) {
        listeners.remove(key)
    }

    /**
     * Self signs an origin block to the devices origin chain.
     *
     * @param flag The optional flag to use when self signing.
     */
    @ExperimentalUnsignedTypes
    fun selfSignOriginChain (flag: Int?) : Deferred<Unit> = GlobalScope.async {
        val bitFlag = flag ?: 0
        val options = getBoundWitnessOptions(bitFlag).await()
        val boundWitness = XyoZigZagBoundWitness(originState.getSigners(), makePayload(options.toTypedArray()).await())
        boundWitness.incomingData(null, true).await()
        updateOriginState(boundWitness).await()
        onBoundWitnessEndSuccess(boundWitness).await()
    }

    fun addBoundWitnessOption (boundWitnessOption: XyoBoundWitnessOption) {
        boundWitnessOptions[boundWitnessOption.flag] = boundWitnessOption
    }

    /**
     * Gets all of the unsigned payloads for a given set of bound witness options.
     *
     * @param options The options to get the unsigned payload from.
     * @return All of the options that comply to that filter.
     */
    private fun getUnSignedPayloads (options: Array<XyoBoundWitnessOption>) = GlobalScope.async {
        val unsignedPayloads = ArrayList<ByteArray>()

        for (option in options) {
            val unsignedPayload = option.getUnsignedPayload()

            if (unsignedPayload != null) {
                unsignedPayloads.add(unsignedPayload)
            }
        }

        return@async unsignedPayloads
    }

    /**
     * Gets all of the signed payloads for a given set of bound witness options.
     *
     * @param options The options to get the signed payload from.
     * @return All of the options that comply to that filter.
     */
    private fun getSignedPayloads (options: Array<XyoBoundWitnessOption>) = GlobalScope.async {
        val signedPayloads = ArrayList<ByteArray>()

        for (option in options) {
            val signedPayload = option.getSignedPayload()

            if (signedPayload != null) {
                signedPayloads.add(signedPayload)
            }
        }

        return@async signedPayloads
    }

    private fun getBoundWitnessOptions (bitFlag: Int) = GlobalScope.async {
        val options = ArrayList<XyoBoundWitnessOption>()

        for ((flag, option) in boundWitnessOptions) {
            if (flag and bitFlag != 0) {
                options.add(option)
            }
        }

        return@async options
    }


    private fun getHeuristics () : Array<ByteArray> {
        val list = LinkedList<ByteArray>()

        for ((_, getter) in heuristics) {
            val heuristic = getter.getHeuristic()

            if (heuristic != null) {
                list.add(heuristic)
            }

        }

       return list.toTypedArray()
    }

    private fun onBoundWitnessStart () {
        for ((_, listener) in listeners) {
            listener.onBoundWitnessStart()
        }
    }

    @ExperimentalUnsignedTypes
    private fun onBoundWitnessEndSuccess (boundWitness: XyoBoundWitness) = GlobalScope.async {
        loadCreatedBoundWitness(boundWitness).await()

        for ((_, listener) in listeners) {
            listener.onBoundWitnessEndSucess(boundWitness)
        }
    }

    @ExperimentalUnsignedTypes
    private fun loadCreatedBoundWitness (boundWitness: XyoBoundWitness) : Deferred<Unit> = GlobalScope.async {
        val hash = boundWitness.getHash(hashingProvider).await()

        if (!originBlocks.containsOriginBlock(hash.self).await()) {
            val subBlocks = getBridgedBlocks(boundWitness)
            // todo remove blocks
            // boundWitness.removeTypeFromUnsigned(XyoBridgeBlockSet.id)
            originBlocks.addBoundWitness(boundWitness).await()

            for ((_, listener) in listeners) {
                listener.onBoundWitnessDiscovered(boundWitness)
            }

            if (subBlocks != null) {
                for (subBlock in subBlocks) {
                    loadCreatedBoundWitness(XyoBoundWitness.getInstance(subBlock)).await()
                }
            }
        }

    }

    @ExperimentalUnsignedTypes
    private fun getBridgedBlocks (boundWitness: XyoBoundWitness) : Iterator<ByteArray>? {
        for (payload in XyoObjectIterator(boundWitness.payloads)) {
            for (item in XyoObjectIterator(payload)) {
                if (XyoObjectSchema.createFromHeader(item.copyOfRange(0, 2)).id == BRIDGE_BLOCK_SET.id) {
                    return XyoObjectIterator(item)
                }
            }
        }
        return null
    }

    private fun onBoundWitnessEndFailure(error: Exception?) {
        currentBoundWitnessSession = null
        for ((_, listener) in listeners) {
            listener.onBoundWitnessEndFailure(error)
        }
    }

    @ExperimentalUnsignedTypes
    protected suspend fun doBoundWitness (startingData : ByteArray?, pipe: XyoNetworkPipe) {
        if (currentBoundWitnessSession != null) return
        onBoundWitnessStart()

        val choice = getChoice(ByteBuffer.allocate(4).put(pipe.peer.getRole()).int, startingData == null)
        val options = getBoundWitnessOptions(choice).await()

        currentBoundWitnessSession = XyoZigZagBoundWitnessSession(
                pipe,
                makePayload(options.toTypedArray()).await(),
                originState.getSigners(),
                ByteBuffer.allocate(4).putInt(choice).array()
        )

        val error = currentBoundWitnessSession?.doBoundWitness(startingData)
        pipe.close().await()

        notifyOptions(options.toTypedArray(), currentBoundWitnessSession)

        if (currentBoundWitnessSession?.completed == true && error == null) {
            updateOriginState(currentBoundWitnessSession!!).await()
            onBoundWitnessEndSuccess(currentBoundWitnessSession!!).await()
            currentBoundWitnessSession = null
            return
        }

        onBoundWitnessEndFailure(error)
        currentBoundWitnessSession = null
    }

    private fun notifyOptions (options: Array<XyoBoundWitnessOption>, boundWitness: XyoBoundWitness?) {
        for (option in options) {
            option.onCompleted(boundWitness)
        }
    }

    private fun updateOriginState (boundWitness: XyoBoundWitness) = GlobalScope.async {
        val hash = boundWitness.getHash(hashingProvider).await()
        originState.newOriginBlock(hash)
    }

    private fun makePayload (options: Array<XyoBoundWitnessOption>) = GlobalScope.async {
        val unsignedPayloads = ArrayList<ByteArray>(getHeuristics().asList())
        val signedPayloads = ArrayList<ByteArray>()
        val previousHash = originState.previousHash
        val index = originState.index
        val nextPublicKey = originState.nextPublicKey

        if (previousHash != null) {
            signedPayloads.add(previousHash)
        }

        if (nextPublicKey != null) {
            signedPayloads.add(nextPublicKey)
        }

        signedPayloads.add(index)

        signedPayloads.addAll(getSignedPayloads(options).await())
        unsignedPayloads.addAll(getUnSignedPayloads(options).await())

        return@async XyoObjectSetCreator.createTypedIterableObject(PAYLOAD, arrayOf(
                XyoObjectSetCreator.createUntypedIterableObject(ARRAY_UNTYPED, signedPayloads.toTypedArray()),
                XyoObjectSetCreator.createUntypedIterableObject(ARRAY_UNTYPED, unsignedPayloads.toTypedArray())
        ))
    }
}