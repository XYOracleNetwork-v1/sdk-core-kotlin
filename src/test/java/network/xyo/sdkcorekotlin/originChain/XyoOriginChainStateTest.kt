package network.xyo.sdkcorekotlin.originChain

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.signing.XyoSigner
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test


class XyoOriginChainStateTest : XyoTestBase() {
    private val numberOfBlocks = 10
    private val hashCreator = XyoSha256
    private val originChainState = XyoOriginChainStateManager(0)
    private var lastHash : XyoHash? = null
    private var nextKey : XyoObject? = null

    @Test
    fun testOriginChainTest () {
        XyoSha1WithSecp256K.enable()
        val startingSigner = XyoSigner.getCreator(0x02)!!.newInstance()
        runBlocking {
            val originBlocks = ArrayList<XyoBoundWitness>()
            originChainState.addSigner(startingSigner)
            for (i in 0..numberOfBlocks) {
                val elementsInSignedPayload = ArrayList<XyoObject>()
                val elementsInUnsignedPayload = arrayOf<XyoObject>(XyoRssi(-65))

                elementsInSignedPayload.add(originChainState.index)

                if (originChainState.nextPublicKey != null) {
                    nextKey = originChainState.nextPublicKey!!
                    elementsInSignedPayload.add(originChainState.nextPublicKey!!)
                }

                if (originChainState.previousHash != null) {
                    elementsInSignedPayload.add(originChainState.previousHash!!)
                }

                val signedPayload = XyoMultiTypeArrayInt(elementsInSignedPayload.toTypedArray())
                val unsignedPayload = XyoMultiTypeArrayInt(elementsInUnsignedPayload)
                val alicePayload = XyoPayload(signedPayload, unsignedPayload)
                val aliceBoundWitness = XyoZigZagBoundWitness(originChainState.getSigners(), alicePayload)

                aliceBoundWitness.incomingData(null, true).await()
                originChainState.newOriginBlock(aliceBoundWitness.getHash(hashCreator).await())
                originBlocks.add(aliceBoundWitness)

                originChainState.addSigner(XyoSigner.getCreator(0x02)!!.newInstance())
                lastHash = aliceBoundWitness.getHash(hashCreator).await()

                if (i != 0) {
                    assertArrayEquals(lastHash!!.hash, originChainState.previousHash!!.hash.hash)
                    originChainState.removeOldestSigner()
                }

                assertEquals(i, originChainState.index.number - 1)
            }
        }
    }
}