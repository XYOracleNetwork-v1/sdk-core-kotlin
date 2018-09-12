package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.single.XyoBridgeBlockSet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue

class XyoBridgingOption (private val hashingProvider : XyoHash.XyoHashProvider): XyoBoundWitnessOption() {
    private var hashOfOriginBlocks : XyoObject? = null
    private var originBlocksToSend : XyoObject? = null

    override val flag: Int = XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN

    override fun getSignedPayload(): XyoObject? {
        return hashOfOriginBlocks
    }

    override fun getUnsignedPayload(): XyoObject? {
        return originBlocksToSend
    }

    fun updateOriginChain(originBlocksToBridge : Array<XyoObject>) = async {
        originBlocksToSend = XyoBridgeBlockSet(originBlocksToBridge)
        val encodedOriginBlocksToSend = originBlocksToSend?.untyped?.value

        if (encodedOriginBlocksToSend != null) {
            val originBlocksToSendTyped = originBlocksToSend
            if (originBlocksToSendTyped is XyoBridgeBlockSet) {
                hashOfOriginBlocks = originBlocksToSendTyped.getHashSet(hashingProvider).await()
            }
            return@async
        }

        hashOfOriginBlocks = null
    }
}