package network.xyo.sdkcorekotlin.originChain

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha1WithSecp256K
import org.junit.Assert

class XyoOriginChainStateTest : XyoTestBase() {
    private val numberOfBlocks = 10
    private val hashCreator = XyoSha256
    private val originChainState = XyoOriginChainStateManager(0)
    private var lastHash : XyoHash? = null
    private var nextKey : XyoObject? = null

    @kotlin.test.Test
    fun testOriginChainTest () {
        XyoSha1WithSecp256K.enable()
        val startingSigner = XyoSigner.getCreator(0x02)!!.newInstance().value!!
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
                originChainState.newOriginBlock(aliceBoundWitness.getHash(hashCreator).await().value!!)
                originBlocks.add(aliceBoundWitness)

                originChainState.addSigner(XyoSigner.getCreator(0x02)!!.newInstance().value!!)
                lastHash = aliceBoundWitness.getHash(hashCreator).await().value!!

                if (i != 0) {
                    Assert.assertArrayEquals(lastHash!!.hash, originChainState.previousHash!!.hash.hash)
                    originChainState.removeOldestSigner()
                }

                Assert.assertEquals(i, originChainState.index.number - 1)
            }
        }
    }
}