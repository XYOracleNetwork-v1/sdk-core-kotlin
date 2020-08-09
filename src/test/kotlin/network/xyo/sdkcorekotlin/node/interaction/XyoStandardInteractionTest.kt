package network.xyo.sdkcorekotlin.node.interaction

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import org.junit.Test

class XyoStandardInteractionTest : XyoTestBase() {

    @Test
    @kotlin.ExperimentalUnsignedTypes
    fun testStandardInteraction () {
        runBlocking {
            /*val nodeOne = createRelayNode()
            val nodeTwo = createRelayNode()

            val server = XyoTcpServer(8080)
            server.listen { pipe ->
                GlobalScope.launch {
                    val handler = XyoNetworkHandler(pipe)
                    nodeTwo.boundWitness(handler, XyoBoundWitnessCatalog).await()
                }
            }

            val socket = Socket("localhost", 8080)
            val pipe = XyoTcpPipe(socket, null)
            val handler = XyoNetworkHandler(pipe)
            nodeOne.boundWitness(handler, XyoBoundWitnessCatalog).await()
            handler.pipe.close().await()*/
        }
    }

    @Test
    @kotlin.ExperimentalUnsignedTypes
    fun testByteErrorInteraction () {
        runBlocking {
            /*val nodeOne = createRelayNode()
            val server = XyoTcpServer(8081)
            server.listen { pipe ->
                GlobalScope.launch {
                    val handler = XyoNetworkHandler(pipe)
                    handler.sendChoicePacket(byteArrayOf(0x01), byteArrayOf(0x01, 0x01, 0x08)).await()
                }
            }

            val socket = Socket("127.0.0.1", 8081)
            val pipe = XyoTcpPipe(socket, null)
            val handler = XyoNetworkHandler(pipe)
            nodeOne.boundWitness(handler, XyoBoundWitnessCatalog).await()
            handler.pipe.close().await()*/
        }
    }

    @Test
    @kotlin.ExperimentalUnsignedTypes
    fun testNotFetterOrWitnessInteraction () {
        runBlocking {
            /*val nodeOne = createRelayNode()
            val server = XyoTcpServer(8082)
            server.listen { pipe ->
                GlobalScope.launch {
                    val handler = XyoNetworkHandler(pipe)
                    val transfer = XyoIterableObject.createTypedIterableObject(XyoSchemas.FETTER_SET, arrayOf(XyoBuff.newInstance(XyoSchemas.RSSI, byteArrayOf(0x00))))
                    handler.sendChoicePacket(byteArrayOf(0x01), transfer.bytesCopy).await()
                }
            }

            val socket = Socket("0.0.0.0", 8082)
            val pipe = XyoTcpPipe(socket, null)
            val handler = XyoNetworkHandler(pipe)
            nodeOne.boundWitness(handler, XyoBoundWitnessCatalog).await()
            handler.pipe.close().await()*/
        }
    }


}