package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha3
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer


class XyoOriginChainStateTest : XyoTestBase() {
    private val numberOfBlocks = 10
    private val hashCreator = XyoSha3
    private val originChainState = XyoOriginChainStateManager(0)
    private var lastHash : XyoHash? = null
    private var nextKey : XyoBuff? = null

    @Test
    fun testOriginChainTest () {
        val startingSigner = XyoSha256WithSecp256K.newInstance()

        runBlocking {
            val originBlocks = ArrayList<XyoBoundWitness>()
            originChainState.addSigner(startingSigner)
            for (i in 0..numberOfBlocks) {
                val elementsInSignedPayload = ArrayList<XyoBuff>()
                val elementsInUnsignedPayload = arrayOf<XyoBuff>()


                elementsInSignedPayload.add(originChainState.index)

                val nextPublicKey = originChainState.nextPublicKey
                if (nextPublicKey != null) {
                    nextKey = nextPublicKey
                    elementsInSignedPayload.add(nextPublicKey)
                }

                val hash = originChainState.previousHash
                if (hash != null) {
                    elementsInSignedPayload.add(hash)
                }

                val aliceBoundWitness = XyoZigZagBoundWitness(
                        originChainState.getSigners(),
                        elementsInSignedPayload.toTypedArray(),
                        elementsInUnsignedPayload
                )

                aliceBoundWitness.incomingData(null, true).await()
                originChainState.newOriginBlock(aliceBoundWitness.getHash(hashCreator).await())
                originBlocks.add(aliceBoundWitness)

                originChainState.addSigner(XyoSha256WithSecp256K.newInstance())
                lastHash = aliceBoundWitness.getHash(hashCreator).await()

                if (i != 0) {
                    assertArrayEquals(lastHash?.bytesCopy, originChainState.previousHash!!.valueCopy)
                    originChainState.removeOldestSigner()
                }

                assertEquals(i, ByteBuffer.wrap((originChainState.index.valueCopy)).int - 1 )
            }
        }
    }
}