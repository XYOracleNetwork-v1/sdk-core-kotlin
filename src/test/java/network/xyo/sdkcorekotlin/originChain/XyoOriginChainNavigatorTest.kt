package network.xyo.sdkcorekotlin.originChain

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoOriginChainNavigator
import network.xyo.sdkcorekotlin.XyoOriginChainStateManager
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha1WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1CompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.storage.XyoInMemoryStorageProvider
import org.junit.Assert

class XyoOriginChainNavigatorTest : XyoTestBase() {
    private val numberOfBlocks = 10
    private val hashCreator = XyoSha256
    private val originNavigator = XyoOriginChainNavigator(XyoInMemoryStorageProvider(), hashCreator)
    private val originChainState = XyoOriginChainStateManager(0)
    private var lastHash : XyoHash? = null
    private var nextKey : XyoObject? = null

    private fun createOriginChain () {
        XyoSha1WithSecp256K.enable()
        XyoSha256WithSecp256K.enable()
        XyoSecp256K1CompressedPublicKey.enable()
        XyoPayload.enable()
        XyoKeySet.enable()
        XyoRssi.enable()
        XyoSha256WithEcdsaSignature.enable()
        XyoSignatureSet.enable()
        XyoNextPublicKey.enable()
        XyoIndex.enable()
        XyoPreviousHash.enable()
        XyoSha256.enable()

        XyoKeySet.enable()
        val startingSigner = XyoSha256WithSecp256K.newInstance().value!!
        runBlocking {
            originChainState.addSigner(startingSigner)
            for (i in 0..numberOfBlocks) {
                val elementsInSignedPayload = ArrayList<XyoObject>()
                val elementsInUnsignedPayload = arrayOf<XyoObject>(XyoRssi(-65))

                elementsInSignedPayload.add(originChainState.index)
                elementsInSignedPayload.add(XyoRssi(-65))

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
                originNavigator.addBoundWitness(aliceBoundWitness).await()

                originChainState.addSigner(XyoSha256WithSecp256K.newInstance().value!!)
                lastHash = aliceBoundWitness.getHash(hashCreator).await().value!!


                val packedBoundWitness = aliceBoundWitness.untyped
                if (packedBoundWitness.error != null) throw Exception("packedBoundWitness Error!")
                val packedBoundWitnessValue = packedBoundWitness.value ?: throw Exception("Value is null!")

                val recreated = XyoBoundWitness.createFromPacked(packedBoundWitnessValue)
                Assert.assertArrayEquals(recreated.value!!.untyped.value!!, packedBoundWitness.value!!)
            }
        }
    }

    @kotlin.test.Test
    fun testOriginChainNavigatorTest () {
        runBlocking {
            createOriginChain()

            val startingHash = originChainState.allHashes[5].typed.value!!
            val originBlockStart = originNavigator.getOriginBlockByBlockHash(startingHash).await()
            val originBlockValue = originBlockStart.value ?: throw Exception("Origin Block is null!")
            val previousBlocks = originBlockValue.findPreviousBlocks().await()
            val previousBlocksValue = previousBlocks.value ?: throw Exception("Previous Blocks is null!")

            val firstPreviousHash = previousBlocksValue[0]!!

            val previousBlock = originNavigator.getOriginBlockByBlockHash(firstPreviousHash).await()
            val previousBlockValue = previousBlock.value ?: throw Exception("Previous Block is null!")

            val parentOfPreviousBlockHash = previousBlockValue.getHash().await()
            val parentOfPreviousBlockHashValue = parentOfPreviousBlockHash.value ?: throw Exception("Previous Block Parent Hash is null!")

            val originalBlock = originNavigator.getOriginBlockByPreviousHash(parentOfPreviousBlockHashValue.typed.value!!).await()
            val originalBlockValue = originalBlock.value ?: throw Exception("Original Block is null!")

            Assert.assertArrayEquals(originalBlockValue.boundWitness.untyped.value!!, originBlockValue.boundWitness.untyped.value!!)
        }
    }
}