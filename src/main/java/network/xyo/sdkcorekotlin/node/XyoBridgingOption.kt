package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue

class XyoBridgingOption (private val hashingProvider : XyoHash.XyoHashProvider): XyoBoundWitnessOption() {
    private var hashOfOriginBlocks : XyoObject? = null
    private var originBlocksToSend : XyoObject? = null

    override val flag: Int = XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN

    override fun getSignedPayload(): XyoObject? {
        return hashOfOriginBlocks
    }

    override fun getUnsignedPayload(): XyoObject? {
        return originBlocksToSend
    }

    suspend fun updateOriginChain(originBlocksToBridge : Array<XyoObject>) {
        originBlocksToSend = XyoSingleTypeArrayInt(
                XyoBoundWitness.major,
                XyoBoundWitness.minor,
                originBlocksToBridge
        )

        val encodedOriginBlocksToSend = originBlocksToSend?.untyped?.value

        if (encodedOriginBlocksToSend != null) {
            hashOfOriginBlocks = hashingProvider.createHash(encodedOriginBlocksToSend).await().value
            return
        }

        hashOfOriginBlocks = null
    }
}