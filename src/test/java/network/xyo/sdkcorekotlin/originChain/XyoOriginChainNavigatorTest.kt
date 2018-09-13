package network.xyo.sdkcorekotlin.originChain

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.origin.XyoOriginChainNavigator
import network.xyo.sdkcorekotlin.origin.XyoOriginChainStateManager
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
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256kSha256WithEcdsaSignature
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
        XyoSecp256K1UnCompressedPublicKey.enable()
        XyoPayload.enable()
        XyoKeySet.enable()
        XyoRssi.enable()
        XyoSecp256kSha256WithEcdsaSignature.enable()
        XyoSignatureSet.enable()
        XyoNextPublicKey.enable()
        XyoIndex.enable()
        XyoPreviousHash.enable()
        XyoSha256.enable()

        XyoKeySet.enable()
        val startingSigner = XyoSha256WithSecp256K.newInstance()
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
                originChainState.newOriginBlock(aliceBoundWitness.getHash(hashCreator).await())
                originNavigator.addBoundWitness(aliceBoundWitness).await()

                originChainState.addSigner(XyoSha256WithSecp256K.newInstance())
                lastHash = aliceBoundWitness.getHash(hashCreator).await()


                val packedBoundWitness = aliceBoundWitness.untyped
                val recreated = XyoBoundWitness.createFromPacked(packedBoundWitness)
                Assert.assertArrayEquals(recreated.untyped, packedBoundWitness)
            }
        }
    }

    @kotlin.test.Test
    fun testOriginChainNavigatorTest () {
        runBlocking {
            createOriginChain()

            val startingHash = originChainState.allHashes[5].typed
            val originBlockStart = originNavigator.getOriginBlockByBlockHash(startingHash).await()
            val originBlockValue = originBlockStart ?: throw Exception("Origin Block is null!")
            val previousBlocks = originBlockValue.findPreviousBlocks().await()
            val firstPreviousHash = previousBlocks[0]

            val previousBlock = originNavigator.getOriginBlockByBlockHash(firstPreviousHash!!).await()
            val previousBlockValue = previousBlock ?: throw Exception("Previous Block is null!")

            val parentOfPreviousBlockHash = previousBlockValue.getHash().await()

            val originalBlock = originNavigator.getOriginBlockByPreviousHash(parentOfPreviousBlockHash.typed).await()
            val originalBlockValue = originalBlock ?: throw Exception("Original Block is null!")

            Assert.assertArrayEquals(originalBlockValue.boundWitness.untyped, originBlockValue.boundWitness.untyped)
        }
    }
}