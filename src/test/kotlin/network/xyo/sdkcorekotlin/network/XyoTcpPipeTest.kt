package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.*
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.hashing.XyoBasicHashBase
import network.xyo.sdkcorekotlin.network.tcp.XyoTcpPipe
import network.xyo.sdkcorekotlin.node.XyoRelayNode
import network.xyo.sdkcorekotlin.persist.XyoInMemoryStorageProvider
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageBridgeQueueRepository
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginBlockRepository
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginStateRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import org.junit.Test
import java.net.Socket
import kotlin.experimental.or

class XyoTcpPipeTest : XyoTestBase( ){

    @Test
    fun testTest () {

        val key = XyoSha256WithSecp256K.newInstance()
        println(key.publicKey.bytesCopy.toHexString())

    }

    private val testProcedureCatalogue= object : XyoProcedureCatalog {
        override fun canDo(byteArray: ByteArray): Boolean {
            return true
        }

        override fun choose(byteArray: ByteArray): ByteArray {
            return byteArrayOf(XyoProcedureCatalogFlags.BOUND_WITNESS.toByte())
        }

        override fun getEncodedCanDo(): ByteArray {
            // return byteArrayOf(0x00, 0x00, 0x00, 0x01)
            return byteArrayOf(0x00, 0x00, 0x00, XyoProcedureCatalogFlags.GIVE_ORIGIN_CHAIN.toByte() or XyoProcedureCatalogFlags.TAKE_ORIGIN_CHAIN.toByte() or XyoProcedureCatalogFlags.GIVE_ORIGIN_CHAIN.toByte())
        }
    }

    @Test
    @kotlin.ExperimentalUnsignedTypes
    fun testTcpBoundWitness () {
        val enabled = false
        // a node must be running on localhost:11000, for this test to pass
        if (enabled) {
            runBlocking {

                val storage = XyoInMemoryStorageProvider()
                val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
                val originQueueRepo = XyoStorageOriginBlockRepository(storage, hasher)
                val originStateRepo = XyoStorageOriginStateRepository(storage)
                val bridgeQueueRepo = XyoStorageBridgeQueueRepository(storage)
                val node = XyoRelayNode(originQueueRepo, originStateRepo, bridgeQueueRepo, hasher)
                val signer = XyoSha256WithSecp256K.newInstance()
                node.originState.addSigner(signer)
                node.selfSignOriginChain()


                while (true) {
                    withContext(Dispatchers.IO) {
                        val socket = Socket("localhost", 7777)
                        val pipe = XyoTcpPipe(socket, null)
                        val handler = XyoNetworkHandler(pipe)

                        val bw = node.boundWitness(handler, testProcedureCatalogue).await()
                        println("BOUND WITNESS DONE: " + bw?.getHash(hasher)?.bytesCopy?.toHexString())

                        val all = originQueueRepo.getAllOriginBlockHashes()!!

                        while (all.hasNext()) {
                            println("BLOCK IN INDEX: " + all.next().bytesCopy.toHexString())
                        }

                        delay(1_000)
                    }
                }
            }

        }

    }
}