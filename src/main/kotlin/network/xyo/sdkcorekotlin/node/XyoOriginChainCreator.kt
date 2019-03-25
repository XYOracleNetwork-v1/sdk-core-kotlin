package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.boundWitness.*
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.heuristics.XyoHeuristicGetter
import network.xyo.sdkcorekotlin.network.*
import network.xyo.sdkcorekotlin.persist.repositories.XyoIndexableOriginBlockRepository
import network.xyo.sdkcorekotlin.origin.XyoOriginBoundWitnessUtil
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BRIDGE_BLOCK_SET
import network.xyo.sdkcorekotlin.persist.XyoStorageProviderInterface
import network.xyo.sdkcorekotlin.repositories.XyoOriginBlockRepository
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.experimental.and
import kotlin.math.min
import kotlin.math.sign

/**
 * A base class for all things creating an managing an origin chain (e.g. Sentinel, Bridge).
 *
 * @param storageProvider A place to store all origin blocks.
 * @property hashingProvider A hashing provider to use hashing utilises.
 */
class XyoOriginChainCreator (val blockRepository: XyoOriginBlockRepository,
                                      val stateRepository: XyoOriginChainStateRepository,
                                      private val hashingProvider : XyoHash.XyoHashProvider) {

    private val boundWitnessOptions = ConcurrentHashMap<Int, XyoBoundWitnessOption>()
    private val heuristics = ConcurrentHashMap<String, XyoHeuristicGetter>()
    private val listeners = ConcurrentHashMap<String, XyoNodeListener>()
    private var currentBoundWitnessSession : XyoZigZagBoundWitnessSession? = null

    val originState = XyoOriginChainStateManager(stateRepository)

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
                originState.signers,
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
            val optionPayload = option.getPayload()
            val unsignedPayload = optionPayload?.signedPayload
            val signedPayload = optionPayload?.unsignedPayload

            if (unsignedPayload != null) {
                unsignedPayloads.add(unsignedPayload)
            }

            if (signedPayload != null) {
                signedPayloads.add(signedPayload)
            }
        }

        return@async XyoOptionPayload(unsignedPayloads.toTypedArray(), signedPayloads.toTypedArray())
    }

    private fun getBoundWitnessOptions (flags: ByteArray): Array<XyoBoundWitnessOption> {
        val options = ArrayList<XyoBoundWitnessOption>()

        for ((_, option) in boundWitnessOptions) {
            if (min(option.flag.size, flags.size) != 0) {
                for (i in 0..(min(option.flag.size, flags.size) - 1)) {
                    val otherCatSection = option.flag[option.flag.size - i - 1]
                    val thisCatSection = flags[flags.size - i - 1]

                    if (otherCatSection and thisCatSection != 0.toByte()) {
                        options.add(option)
                    }
                }
            }
        }

        return options.toTypedArray()
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
            listener.onBoundWitnessEndSuccess(boundWitness)
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

        if (!blockRepository.containsOriginBlock(hash).await()) {
            val subBlocks = XyoOriginBoundWitnessUtil.getBridgedBlocks(boundWitness)
            val boundWitnessWithoutBlocks = XyoBoundWitness.getInstance(
                    XyoBoundWitnessUtil.removeTypeFromUnsignedPayload(BRIDGE_BLOCK_SET.id, boundWitness).bytesCopy
            )

            blockRepository.addBoundWitness(boundWitnessWithoutBlocks).await()

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

    private fun getEncodedChoice (bytes: ByteArray) : Int {
        when (bytes.size) {
            1 -> return ByteBuffer.wrap(bytes).get().toInt()
            2 -> return ByteBuffer.wrap(bytes).short.toInt()
            4 -> return ByteBuffer.wrap(bytes).int
        }

        throw XyoBoundWitnessCreationException("Can not read choice.")
    }

    fun boundWitness (handler: XyoNetworkHandler, procedureCatalogue: XyoNetworkProcedureCatalogueInterface): Deferred<XyoBoundWitness?> = GlobalScope.async {
        if (currentBoundWitnessSession != null) {
            onBoundWitnessEndFailure(XyoBoundWitnessCreationException("Bound witness is session"))
            return@async null
        }

        onBoundWitnessStart()

        if (handler.pipe.initiationData == null) {
            // is client

            val responseWithChoice = handler.sendCataloguePacket(procedureCatalogue.getEncodedCanDo()).await()

            if (responseWithChoice == null) {
                onBoundWitnessEndFailure(XyoBoundWitnessCreationException("Response is null"))
                return@async null
            }

            val adv = XyoChoicePacket(responseWithChoice)
            val startingData = createStartingData(adv.getResponse())
        }

        return@async null
    }

    private suspend fun doBoundWitnessWithPipe (pipe: XyoNetworkPipe,
                                                startingData: XyoIterableObject?,
                                                choice: ByteArray): XyoBoundWitness? {

        val options = getBoundWitnessOptions(choice).await()

        currentBoundWitnessSession = XyoZigZagBoundWitnessSession(
                pipe,
                makeSignedPayload(options.toTypedArray()).await(),
                makeUnsignedPayload(options.toTypedArray()).await(),
                originState.signers,
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
            return null
        }

        onBoundWitnessEndFailure(error)
        currentBoundWitnessSession = null
        return null
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
        signedPayloads.addAll(originState.statics)

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