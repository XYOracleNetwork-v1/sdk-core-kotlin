package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.XyoException
import network.xyo.sdkcorekotlin.log.XyoLog
import network.xyo.sdkcorekotlin.boundWitness.*
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.heuristics.XyoHeuristicGetter
import network.xyo.sdkcorekotlin.network.*
import network.xyo.sdkcorekotlin.origin.XyoOriginBoundWitnessUtil
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.schemas.XyoSchemas.BRIDGE_BLOCK_SET
import network.xyo.sdkcorekotlin.repositories.XyoOriginBlockRepository
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.experimental.and
import kotlin.math.min

/**
 * A base class for all things creating an managing an origin chain (e.g. Sentinel, Bridge).
 *
 * @param storageProvider A place to store all origin blocks.
 * @property hashingProvider A hashing provider to use hashing utilities.
 */
open class XyoOriginChainCreator (val blockRepository: XyoOriginBlockRepository,
                                  val stateRepository: XyoOriginChainStateRepository,
                                  private val hashingProvider : XyoHash.XyoHashProvider) {

    private val boundWitnessOptions = ConcurrentHashMap<String, XyoBoundWitnessOption>()
    private val heuristics = ConcurrentHashMap<String, XyoHeuristicGetter>()
    private val listeners = ConcurrentHashMap<String, XyoNodeListener>()
    private var currentBoundWitnessSession : XyoZigZagBoundWitnessSession? = null

    val originState = XyoOriginChainStateManager(stateRepository)

    /**
     * Adds a heuristic to be used when creating bound witnesses.
     *
     * @param key The key for the heuristic.
     * @param heuristic The heuristic to use in bound witnesses.
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
     * @param listener The XyoNodeListener to callback to.
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
     * Self signs an origin block to the device's origin chain.
     *
     * @param flag The optional flag to use when self signing.
     */
    suspend fun selfSignOriginChain () {
        val boundWitness = XyoZigZagBoundWitness(
                originState.signers,
                makeSignedPayload().toTypedArray(),
                arrayOf()
        )
        boundWitness.incomingData(null, true)
        updateOriginState(boundWitness)
        onBoundWitnessEndSuccess(boundWitness)
    }

    fun addBoundWitnessOption (key: String,  boundWitnessOption: XyoBoundWitnessOption) {
        boundWitnessOptions[key] = boundWitnessOption
    }

    private class XyoOptionPayload (val unsignedOptions : Array<XyoObjectStructure>, val signedOptions : Array<XyoObjectStructure> )

    private suspend fun getBoundWitnessOptionPayloads (options: Array<XyoBoundWitnessOption>) : XyoOptionPayload {
        val signedPayloads =  ArrayList<XyoObjectStructure>()
        val unsignedPayloads = ArrayList<XyoObjectStructure>()

        for (option in options) {
            val optionPayload = option.getPayload()
            val unsignedPayload = optionPayload?.unsignedPayload
            val signedPayload = optionPayload?.signedPayload

            if (unsignedPayload != null) {
                unsignedPayloads.add(unsignedPayload)
            }

            if (signedPayload != null) {
                signedPayloads.add(signedPayload)
            }
        }

        return XyoOptionPayload(unsignedPayloads.toTypedArray(), signedPayloads.toTypedArray())
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


    private fun getHeuristics () : Array<XyoObjectStructure> {
        val list = LinkedList<XyoObjectStructure>()

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

    private suspend fun onBoundWitnessEndSuccess (boundWitness: XyoBoundWitness) {
        loadCreatedBoundWitness(boundWitness)

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


    private suspend fun loadCreatedBoundWitness (boundWitness: XyoBoundWitness) {
        val hash = boundWitness.getHash(hashingProvider)

        if (!blockRepository.containsOriginBlock(hash)) {
            val subBlocks = XyoOriginBoundWitnessUtil.getBridgedBlocks(boundWitness)
            val boundWitnessWithoutBlocks = XyoBoundWitness.getInstance(
                    XyoBoundWitnessUtil.removeTypeFromUnsignedPayload(BRIDGE_BLOCK_SET.id, boundWitness).bytesCopy
            )

            blockRepository.addBoundWitness(boundWitnessWithoutBlocks)

            for ((_, listener) in listeners) {
                listener.onBoundWitnessDiscovered(boundWitnessWithoutBlocks)
            }

            if (subBlocks != null) {
                for (subBlock in subBlocks) {
                    XyoLog.logSpecial("Found Bridge Block", TAG)
                    loadCreatedBoundWitness(XyoBoundWitness.getInstance(subBlock.bytesCopy))
                }
            }
        }

    }

    @kotlin.ExperimentalUnsignedTypes
    suspend fun boundWitness (handler: XyoNetworkHandler, procedureCatalogue: XyoProcedureCatalog): XyoBoundWitness? {
        try {
            if (currentBoundWitnessSession != null) {
                onBoundWitnessEndFailure(XyoBoundWitnessCreationException("Busy - Bound witness in progress"))
                return null
            }

            onBoundWitnessStart()

            if (handler.pipe.initiationData == null) {
                // is client

                val responseWithChoice = handler.sendCataloguePacket(procedureCatalogue.getEncodedCanDo())

                if (responseWithChoice == null) {
                    onBoundWitnessEndFailure(XyoBoundWitnessCreationException("Response is null"))
                    return null
                }

                val adv = XyoChoicePacket(responseWithChoice)
                val startingData = createStartingData(adv.getResponse())

                return doBoundWitnessWithPipe(handler, startingData, adv.getChoice())
            }

            val choice = procedureCatalogue.choose(XyoProcedureCatalogFlags.flip(handler.pipe.initiationData!!.getChoice()))
            return doBoundWitnessWithPipe(handler, null, choice)
        } catch (e: XyoObjectException) {
            onBoundWitnessEndFailure(e)
        } catch (e: XyoException) {
            onBoundWitnessEndFailure(e)
        }

        return null
    }

    private suspend fun doBoundWitnessWithPipe (handler: XyoNetworkHandler,
                                                startingData: XyoIterableStructure?,
                                                choice: ByteArray): XyoBoundWitness? {

        val options = getBoundWitnessOptions(choice)
        val payloads = getBoundWitnessOptionPayloads(options)
        val signedPayload = makeSignedPayload()
        signedPayload.addAll(payloads.signedOptions)
        signedPayload.addAll(handler.pipe.getNetworkHeuristics())

        val bw = XyoZigZagBoundWitnessSession(
                handler,
                signedPayload.toTypedArray(),
                payloads.unsignedOptions,
                originState.signers,
                XyoProcedureCatalogFlags.flip(choice)
        )

        currentBoundWitnessSession = bw

        val error = currentBoundWitnessSession?.doBoundWitness(startingData)
        handler.pipe.close()

        notifyOptions(options, currentBoundWitnessSession)

        if (currentBoundWitnessSession?.completed == true && error == null) {
            XyoLog.logSpecial("Created Bound Witness", TAG)
            updateOriginState(currentBoundWitnessSession!!)
            onBoundWitnessEndSuccess(currentBoundWitnessSession!!)
            currentBoundWitnessSession = null
            return bw
        }

        onBoundWitnessEndFailure(error)
        currentBoundWitnessSession = null
        return null
    }

    private fun createStartingData (startingData : ByteArray?) : XyoIterableStructure? {
        if (startingData == null) return null

        return XyoIterableStructure(startingData, 0)
    }

    private fun notifyOptions (options: Array<XyoBoundWitnessOption>, boundWitness: XyoBoundWitness?) {
        for (option in options) {
            option.onCompleted(boundWitness)
        }
    }

    private suspend fun updateOriginState (boundWitness: XyoBoundWitness) {
        val hash = boundWitness.getHash(hashingProvider)
        originState.newOriginBlock(hash)
        originState.repo.commit()
        XyoLog.logSpecial("Updating Origin State. Awaiting Index: ${ByteBuffer.wrap(originState.index.valueCopy).int}", TAG)
    }

    private fun makeSignedPayload (): ArrayList<XyoObjectStructure> {
        val signedPayloads = ArrayList<XyoObjectStructure>(getHeuristics().asList())
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
        signedPayloads.addAll(originState.statics)

        return signedPayloads
    }

    companion object {
        const val TAG = "NOD"
    }
}
