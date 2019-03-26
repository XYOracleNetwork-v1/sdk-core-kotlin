package network.xyo.sdkcorekotlin.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.stub.XyoStubSigner
import network.xyo.sdkcorekotlin.hashing.XyoBasicHashBase
import network.xyo.sdkcorekotlin.network.tcp.XyoTcpPipe
import network.xyo.sdkcorekotlin.node.XyoOriginChainCreator
import network.xyo.sdkcorekotlin.node.XyoRelayNode
import network.xyo.sdkcorekotlin.persist.XyoInMemoryStorageProvider
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageBridgeQueueRepository
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginBlockRepository
import network.xyo.sdkcorekotlin.persist.repositories.XyoStorageOriginStateRepository
import network.xyo.sdkcorekotlin.repositories.XyoOriginBlockRepository
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.junit.Test
import java.net.Socket

class XyoTcpPipeTest : XyoTestBase( ){

    private val testProcedureCatalogue= object : XyoNetworkProcedureCatalogueInterface {
        override fun canDo(byteArray: ByteArray): Boolean {
            return true
        }

        override fun choose(byteArray: ByteArray): ByteArray {
            return byteArrayOf(XyoProcedureCatalogue.BOUND_WITNESS.toByte())
        }

        override fun getEncodedCanDo(): ByteArray {
            return byteArrayOf(0x00, 0x00, 0x00, XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN.toByte())
        }

        override fun getNetworlHuerestics(): Array<XyoBuff> {
            return arrayOf()
        }
    }

    @Test
    fun testTcpBoundWitness () {
        // a node must be running on localhost:11000, for this test to pass
        if (false) {
            runBlocking {
                val storage = XyoInMemoryStorageProvider()
                val hasher = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")
                val originQueueRepo = XyoStorageOriginBlockRepository(storage, hasher)
                val originStateRepo = XyoStorageOriginStateRepository(storage)
                val bridgeQueueRepo = XyoStorageBridgeQueueRepository(storage)
                val node = XyoRelayNode(originQueueRepo, originStateRepo, bridgeQueueRepo, hasher)
                val signer = XyoStubSigner()

                node.originState.addSigner(signer)

                while (true) {
                    val socket = Socket("localhost", 11000)
                    val pipe = XyoTcpPipe(socket, null)
                    val handler = XyoNetworkHandler(pipe)

                    node.boundWitness(handler, testProcedureCatalogue).await()
                    delay(1_000)
                }
            }

        }

    }
}