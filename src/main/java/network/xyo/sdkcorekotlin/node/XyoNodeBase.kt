package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.XyoLog
import network.xyo.sdkcorekotlin.boundWitness.*
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoBasicHashBase
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.origin.XyoIndexableOriginBlockRepository
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BRIDGE_BLOCK_SET
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import network.xyo.sdkobjectmodelkotlin.objects.toHexString
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
    fun selfSignOriginChain (flag: Int?) : Deferred<Unit> = GlobalScope.async {
        val bitFlag = flag ?: 0
        val options = getBoundWitnessOptions(bitFlag).await()
        val boundWitness = XyoZigZagBoundWitness(
                originState.getSigners(),
                makeSignedPayload(options.toTypedArray()).await(),
                makeUnsignedPayload(options.toTypedArray()).await()
        )
        boundWitness.incomingData(null, true).await()
        updateOriginState(boundWitness).await()
        onBoundWitnessEndSuccess(boundWitness).await()
    }

    fun addBoundWitnessOption (boundWitnessOption: XyoBoundWitnessOption) {
        boundWitnessOptions[boundWitnessOption.flag] = boundWitnessOption
    }

    private class XyoOptionPayload (val unsignedOptions : Array<XyoBuff>, val signedOptions : Array<XyoBuff> )

    private fun getBoundWitnessOptionPayloads (options: Array<XyoBoundWitnessOption>) : Deferred<XyoOptionPayload> = GlobalScope.async {
        val signedPayloads =  ArrayList<XyoBuff>()
        val unsignedPayloads = ArrayList<XyoBuff>()

        for (option in options) {
            val unsignedPayload = option.getUnsignedPayload()
            val signedPayload = option.getSignedPayload()

            if (unsignedPayload != null) {
                unsignedPayloads.add(unsignedPayload)
            }

            if (signedPayload != null) {
                signedPayloads.add(signedPayload)
            }
        }

        return@async XyoOptionPayload(signedPayloads.toTypedArray(), unsignedPayloads.toTypedArray())
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


    private fun getHeuristics () : Array<XyoBuff> {
        val list = LinkedList<XyoBuff>()

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

    private fun onBoundWitnessEndSuccess (boundWitness: XyoBoundWitness) = GlobalScope.async {
        loadCreatedBoundWitness(boundWitness).await()

        for ((_, listener) in listeners) {
            listener.onBoundWitnessEndSucess(boundWitness)
        }
    }

    private fun onBoundWitnessEndFailure(error: Exception?) {
        currentBoundWitnessSession = null
        for ((_, listener) in listeners) {
            listener.onBoundWitnessEndFailure(error)
        }
    }


    private fun loadCreatedBoundWitness (boundWitness: XyoBoundWitness) : Deferred<Unit> = GlobalScope.async {
        val hash = boundWitness.getHash(hashingProvider).await()

        if (!originBlocks.containsOriginBlock(hash).await()) {
            val subBlocks = XyoBoundWitnessUtil.getBridgedBlocks(boundWitness)
            val boundWitnessWithoutBlocks = XyoBoundWitness.getInstance(
                    XyoBoundWitnessUtil.removeTypeFromUnsignedPayload(BRIDGE_BLOCK_SET.id, boundWitness).bytesCopy
            )

            originBlocks.addBoundWitness(boundWitnessWithoutBlocks).await()

            for ((_, listener) in listeners) {
                listener.onBoundWitnessDiscovered(boundWitnessWithoutBlocks)
            }

            if (subBlocks != null) {
                for (subBlock in subBlocks) {
                    XyoLog.logSpecial("Found Bridge Block", TAG)
                    loadCreatedBoundWitness(XyoBoundWitness.getInstance(subBlock.bytesCopy)).await()
                }
            }
        }

    }

    protected suspend fun doBoundWitness (startingData : ByteArray?, pipe: XyoNetworkPipe) {
        if (currentBoundWitnessSession != null) return
        onBoundWitnessStart()

        val choice = getChoice(ByteBuffer.wrap(pipe.peer.getRole()).int, startingData == null)
        val options = getBoundWitnessOptions(choice).await()

        currentBoundWitnessSession = XyoZigZagBoundWitnessSession(
                pipe,
                makeSignedPayload(options.toTypedArray()).await(),
                makeUnsignedPayload(options.toTypedArray()).await(),
                originState.getSigners(),
                ByteBuffer.allocate(4).putInt(choice).array()
        )

        val error = currentBoundWitnessSession?.doBoundWitness(createStartingData(startingData))
        pipe.close().await()

        notifyOptions(options.toTypedArray(), currentBoundWitnessSession)

        if (currentBoundWitnessSession?.completed == true && error == null) {
            XyoLog.logSpecial("Created Bound Witness", TAG)
            updateOriginState(currentBoundWitnessSession!!).await()
            onBoundWitnessEndSuccess(currentBoundWitnessSession!!).await()
            currentBoundWitnessSession = null
            return
        }

        onBoundWitnessEndFailure(error)
        currentBoundWitnessSession = null
    }

    private fun createStartingData (startingData : ByteArray?) : XyoIterableObject? {
        if (startingData == null) return null

        return object : XyoIterableObject() {
            override val allowedOffset: Int = 0
            override var item: ByteArray = startingData
        }
    }

    private fun notifyOptions (options: Array<XyoBoundWitnessOption>, boundWitness: XyoBoundWitness?) {
        for (option in options) {
            option.onCompleted(boundWitness)
        }
    }

    private fun updateOriginState (boundWitness: XyoBoundWitness) = GlobalScope.async {
        val hash = boundWitness.getHash(hashingProvider).await()
        originState.newOriginBlock(hash)
        XyoLog.logSpecial("Updating Origin State. Awaiting Index: ${ByteBuffer.wrap(originState.index.valueCopy).int}", TAG)
    }

    private fun makeSignedPayload (options: Array<XyoBoundWitnessOption>) = GlobalScope.async {
        val payloads = getBoundWitnessOptionPayloads(options).await()
        val signedPayloads = ArrayList<XyoBuff>(getHeuristics().asList())
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
        signedPayloads.addAll(payloads.signedOptions)

        return@async signedPayloads.toTypedArray()
    }

    private fun makeUnsignedPayload (options: Array<XyoBoundWitnessOption>) = GlobalScope.async {
        val unsignedPayloads = ArrayList<XyoBuff>()
        val payloads = getBoundWitnessOptionPayloads(options).await()

        unsignedPayloads.addAll(payloads.unsignedOptions)

        return@async unsignedPayloads.toTypedArray()
    }

    companion object {
        const val TAG = "NOD"
    }
}