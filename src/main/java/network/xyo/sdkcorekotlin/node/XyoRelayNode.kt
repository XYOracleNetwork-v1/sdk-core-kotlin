package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectProvider
import network.xyo.sdkcorekotlin.data.array.multi.XyoBridgeHashSet
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.network.XyoNetworkProcedureCatalogueInterface
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface

/**
 * A base class for nodes creating data, then relaying it (e.g.) sentinels and bridges.
 *
 * @param storageProvider A place to store all origin blocks.
 * @param hashingProvider A hashing provider to use hashing utilises.
 */
abstract class XyoRelayNode (storageProvider : XyoStorageProviderInterface,
                             private val hashingProvider : XyoHash.XyoHashProvider) : XyoNodeBase(storageProvider, hashingProvider) {

    private val selfToOtherQueue = XyoBridgingOption(storageProvider)
    private val originBlocksToBridge = XyoBridgeQueue()
    private var running = false

    private val mainBoundWitnessListener = object : XyoNodeListener {
        override fun onBoundWitnessEndFailure(error: Exception?) {}
        override fun onBoundWitnessStart() {}

        override fun onBoundWitnessDiscovered(boundWitness: XyoBoundWitness) {
            async {
                originBlocksToBridge.addBlock(boundWitness.getHash(hashingProvider).await().typed)
                val toBridge = originBlocksToBridge.getBlocksToBridge()
                selfToOtherQueue.addHashSet(XyoBridgeHashSet(XyoObjectProvider.encodedToDecodedArray(toBridge)))
            }
        }
    }

    private val bridgeQueueListener = object : XyoBridgeQueue.Companion.XyoBridgeQueueListener {
        override fun onRemove(boundWitnessHash: ByteArray) {
            async {
                originBlocks.removeOriginBlock(boundWitnessHash)
            }
        }
    }

    /**
     * The nodes procedureCatalogue to advertise when it makes connections.
     */
    abstract val procedureCatalogue : XyoNetworkProcedureCatalogueInterface

    /**
     * Gets a XyoNetworkPipe to have a session with.
     *
     * @return The pipe to have a session with.
     */
    abstract suspend fun findSomeoneToTalkTo() : XyoNetworkPipe

    /**
     * Stop the node from doing all operations.
     */
    fun stop () {
        if (running) {
            running = false
        }
    }

    /**
     * Start the node to do all operations.
     */
    fun start () {
        if (!running) {
            running =  true
            loop()
        }
    }

    /**
     * Calls purgeQueue on the current bridging queue. This is useful to call in low
     * memory situations.
     */
    fun purgeQueue (mask : Int) {
        originBlocksToBridge.purgeQueue(mask)
    }

    override fun getChoice(catalog: Int): Int {
        if (catalog and XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN == XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN) {
            return  XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN
        } else if (catalog and XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN == XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN) {
            return XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN
        }

        return XyoProcedureCatalogue.BOUND_WITNESS
    }

    private fun doConnection() = async {
        val connectionToOtherPartyFrom = findSomeoneToTalkTo()
        if (!running) return@async

        if (connectionToOtherPartyFrom.initiationData == null) {
            val whatTheOtherPartyWantsToDo = connectionToOtherPartyFrom.peer.getRole()
            if (procedureCatalogue.canDo(whatTheOtherPartyWantsToDo)) {
                doBoundWitness(null, connectionToOtherPartyFrom)
            } else {
                connectionToOtherPartyFrom.close()
            }
        } else {
            doBoundWitness(connectionToOtherPartyFrom.initiationData, connectionToOtherPartyFrom)
        }
    }

    private fun loop () {
        launch {
            while (running) {
                doConnection().await()
            }
        }
    }

    init {
        addListener(this.toString(), mainBoundWitnessListener)
        addBoundWitnessOption(selfToOtherQueue)
        originBlocksToBridge.addListener(this.toString(), bridgeQueueListener)
    }
}