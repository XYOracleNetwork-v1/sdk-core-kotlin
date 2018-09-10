package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoErrors
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitnessSession
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.network.XyoNetworkProcedureCatalogueInterface
import network.xyo.sdkcorekotlin.origin.XyoOriginChainNavigator
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface

/**
 * A base class for all things creating an managing an origin chain (e.g. Sentinel, Bridge).
 *
 * @param storageProvider A place to store all origin blocks.
 * @param hashingProvider A hashing provider to use hashing utilises.
 */
abstract class XyoNodeBase (storageProvider : XyoStorageProviderInterface,
                            private val hashingProvider : XyoHash.XyoHashProvider) {

    private val heuristics = HashMap<String, XyoObject>()
    private val listeners = HashMap<String, XyoNodeListener>()
    private var currentBoundWitnessSession : XyoBoundWitness? = null

    /**
     * Gets the choice of a catalog from another party.
     *
     * @param catalog The catalog of the other party.
     * @return The choice to preform in the bound witness.
     */
    abstract fun getChoice (catalog : Int) : Int

    /**
     * All of the origin blocks that the node contains.
     */
    open val originBlocks = XyoOriginChainNavigator(storageProvider, hashingProvider)

    /**
     * The current origin state of the origin node.
     */
    open val originState = XyoOriginChainStateManager(0)

    /**
     * Adds a heuristic to be used when creating bound witnesses.
     *
     * @param key The key for the heuristic.
     * @param heuristic The heuristic to use in  bound witnesses.
     */
    fun addHueristic (key: String, heuristic : XyoObject) {
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
    fun addListiner (key : String, listener : XyoNodeListener) {
        listeners[key] = listener
    }

    /**
     * Removes a listener from the current listener pool.
     *
     * @param key The key of the listener to remove.
     */
    fun removeListiner (key : String) {
        listeners.remove(key)
    }

    /**
     * Self signs an origin block to the devices origin chain.
     *
     * @param flag The optional flag to use when self signing.
     */
    fun selfSignOriginChain (flag: Int?) = async {
        val bitFlag = flag ?: 0
        val boundWitness = XyoZigZagBoundWitness(originState.getSigners(), makePayload(bitFlag).await())
        boundWitness.incomingData(null, true)
        onBoundWitnessEndSuccess(boundWitness).await()
    }

    private fun getHeuristics () : Array<XyoObject> {
        return heuristics.values.toTypedArray()
    }

    private fun onBoundWitnessStart () {
        for ((_, listener) in listeners) {
            listener.onBoundWitnessStart()
        }
    }

    private fun onBoundWitnessEndSuccess (boundWitness: XyoBoundWitness?) = async {
        currentBoundWitnessSession = null
        if (boundWitness != null) {
            val hash = boundWitness.getHash(hashingProvider).await()
            val hashValue = hash.value ?: return@async
            originState.newOriginBlock(hashValue)
            originBlocks.addBoundWitness(boundWitness).await()

            for ((_, listener) in listeners) {
                listener.onBoundWitnessEndSuccess()
            }
            return@async
        }
        onBoundWitnessEndFailure()
    }

    private fun onBoundWitnessEndFailure() {
        currentBoundWitnessSession = null
        for ((_, listener) in listeners) {
            listener.onBoundWitnessEndFailure()
        }
    }

    protected suspend fun doBoundWitness (startingData : ByteArray?, pipe: XyoNetworkPipe) {
        var choice = 0

        if (currentBoundWitnessSession == null) {
            onBoundWitnessStart()

            val otherPartyChoice = pipe.peer.getRole().value
            if (otherPartyChoice != null) {
                choice = getChoice(XyoUnsignedHelper.readUnsignedInt(otherPartyChoice))
            }

            currentBoundWitnessSession = XyoZigZagBoundWitnessSession(pipe, makePayload(choice).await(), originState.getSigners(), XyoUnsignedHelper.createUnsignedInt(choice))

            if (currentBoundWitnessSession != null) {
                val error = currentBoundWitnessSession!!.doBoundWitness(startingData).await()
                pipe.close().await()

                if ((error == null || error.errorCode == XyoErrors.ERR_DISCONNECT)
                        && currentBoundWitnessSession?.completed == true) {

                    onBoundWitnessEndSuccess(currentBoundWitnessSession).await()
                } else {
                    onBoundWitnessEndFailure()
                }
            } else {
                throw Exception("Bound witness session is null!")
            }
        }
    }

    private fun makePayload (bitFlag : Int) = async {
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
        signedPayloads.addAll(XyoBoundWitnessOption.getUnSignedPayloads(bitFlag))
        unsignedPayloads.addAll(XyoBoundWitnessOption.getUnSignedPayloads(bitFlag))

        return@async XyoPayload(
                XyoMultiTypeArrayInt(signedPayloads.toTypedArray()),
                XyoMultiTypeArrayInt(unsignedPayloads.toTypedArray())
        )
    }
}