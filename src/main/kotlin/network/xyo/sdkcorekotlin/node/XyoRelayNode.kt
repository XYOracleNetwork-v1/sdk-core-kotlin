package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.network.XyoNetworkProcedureCatalogueInterface
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue
import network.xyo.sdkcorekotlin.storage.XyoStorageProviderInterface
import java.nio.ByteBuffer
import kotlin.concurrent.thread

/**
 * A base class for nodes creating data, then relaying it (e.g.) sentinels and bridges.
 *
 * @param storageProvider A place to store all origin blocks.
 * @property hashingProvider A hashing provider to use hashing utilises.
 */
abstract class XyoRelayNode (storageProvider : XyoStorageProviderInterface,
                             private val hashingProvider : XyoHash.XyoHashProvider) : XyoNodeBase(storageProvider, hashingProvider) {

    val originBlocksToBridge = XyoBridgeQueue()
    private val selfToOtherQueue = XyoBridgingOption(storageProvider, originBlocksToBridge)
    private var running = false

    private val mainBoundWitnessListener = object : XyoNodeListener {
        override fun onBoundWitnessEndFailure(error: Exception?) {}

        override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
            for (hash in originBlocksToBridge.getToRemove()) {
                runBlocking {
                    originBlocks.removeOriginBlock(hash).await()
                }
            }
        }

        override fun onBoundWitnessDiscovered(boundWitness: XyoBoundWitness) {
            runBlocking {
                originBlocksToBridge.addBlock(boundWitness.getHash(hashingProvider).await())
            }
        }

        override fun onBoundWitnessStart() {}
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

    override fun getChoice(catalog: Int, strict: Boolean): Int {
        if (catalog and XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN == XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN
                && procedureCatalogue.canDo(ByteBuffer.allocate(4).putInt(XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN).array())) {
            return  XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN
        } else if (catalog and XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN == XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN
                && procedureCatalogue.canDo(ByteBuffer.allocate(4).putInt(XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN).array())) {
            return XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN
        }

        return XyoProcedureCatalogue.BOUND_WITNESS
    }

    private fun doConnection() = GlobalScope.async {
        val connectionToOtherPartyFrom = findSomeoneToTalkTo()
        if (!running) return@async

        if (connectionToOtherPartyFrom.initiationData == null) {
            val whatTheOtherPartyWantsToDo = connectionToOtherPartyFrom.peer.getRole()
            if (procedureCatalogue.canDo(whatTheOtherPartyWantsToDo)) {
                doBoundWitness(null, connectionToOtherPartyFrom)
            } else {
                connectionToOtherPartyFrom.close().await()
            }
        } else {
            doBoundWitness(connectionToOtherPartyFrom.initiationData, connectionToOtherPartyFrom)
        }
    }

    private fun loop () {
        thread {
            GlobalScope.launch {
                while (running) {
                    doConnection().await()
                }
            }
        }
    }

    init {
        addListener(this.toString(), mainBoundWitnessListener)
        addBoundWitnessOption(selfToOtherQueue)
    }
}
