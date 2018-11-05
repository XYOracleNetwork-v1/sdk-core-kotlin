package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitnessSession
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoBridgeBlockSet
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.origin.XyoIndexableOriginBlockRepository
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import java.util.*
import kotlin.collections.ArrayList

/**
 * A base class for all things creating an managing an origin chain (e.g. Sentinel, Bridge).
 *
 * @param storageProvider A place to store all origin blocks.
 * @param hashingProvider A hashing provider to use hashing utilises.
 */
abstract class XyoNodeBase (storageProvider : XyoStorageProviderInterface,
                            private val hashingProvider : XyoHash.XyoHashProvider) {

    private val boundWitnessOptions = HashMap<Int, XyoBoundWitnessOption>()
    private val heuristics = HashMap<String, XyoHeuristicGetter>()
    private val listeners = HashMap<String, XyoNodeListener>()
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
    fun selfSignOriginChain (flag: Int?) = GlobalScope.async {
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
        val unsignedPayloads = ArrayList<XyoObject>()

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
        val signedPayloads = ArrayList<XyoObject>()

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


    private fun getHeuristics () : Array<XyoObject> {
        val list = LinkedList<XyoObject>()

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

    private fun loadCreatedBoundWitness (boundWitness: XyoBoundWitness) : Deferred<Unit> = GlobalScope.async {
        val hash = boundWitness.getHash(hashingProvider).await()

        if (!originBlocks.containsOriginBlock(hash.typed).await()) {
            val subBlocks : Array<XyoObject> = getBridgedBlocks(boundWitness)
            boundWitness.removeTypeFromUnsigned(XyoBridgeBlockSet.id)
            originBlocks.addBoundWitness(boundWitness).await()

            for ((_, listener) in listeners) {
                listener.onBoundWitnessDiscovered(boundWitness)
            }

            for (subBlock in subBlocks) {
                val subBlockBoundWitness : XyoBoundWitness? = subBlock as? XyoBoundWitness
                if (subBlockBoundWitness != null) {
                    loadCreatedBoundWitness(subBlockBoundWitness).await()
                }
            }
        }

    }

    private fun getBridgedBlocks (boundWitness: XyoBoundWitness) : Array<XyoObject> {
        for (payload in boundWitness.payloads) {
            val bridgeSet = payload.unsignedPayloadMapping[XyoBridgeBlockSet.id.contentHashCode()] as? XyoBridgeBlockSet
            if (bridgeSet != null) {
                return bridgeSet.array
            }
        }
        return arrayOf()
    }

    private fun onBoundWitnessEndFailure(error: Exception?) {
        currentBoundWitnessSession = null
        for ((_, listener) in listeners) {
            listener.onBoundWitnessEndFailure(error)
        }
    }

    protected suspend fun doBoundWitness (startingData : ByteArray?, pipe: XyoNetworkPipe) {
        if (currentBoundWitnessSession != null) return
        onBoundWitnessStart()
        val choice = getChoice(XyoUnsignedHelper.readUnsignedInt(pipe.peer.getRole()), startingData == null)
        val options = getBoundWitnessOptions(choice).await()

        currentBoundWitnessSession = XyoZigZagBoundWitnessSession(
                pipe,
                makePayload(options.toTypedArray()).await(),
                originState.getSigners(),
                XyoUnsignedHelper.createUnsignedInt(choice)
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
        val unsignedPayloads = ArrayList<XyoObject>(getHeuristics().asList())
        val signedPayloads = ArrayList<XyoObject>()
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

        return@async XyoPayload(
                XyoMultiTypeArrayInt(signedPayloads.toTypedArray()),
                XyoMultiTypeArrayInt(unsignedPayloads.toTypedArray())
        )
    }
}