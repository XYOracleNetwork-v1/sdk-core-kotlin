package network.xyo.sdkcorekotlin

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitnessTransfer
import network.xyo.sdkcorekotlin.boundWitness.XyoZigZagBoundWitness
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256

class XyoMultiPartyBoundWitnessTest : XyoTestBase() {
    private val numberOfSigners = 3
    private val payload = XyoPayload(XyoMultiTypeArrayInt(arrayOf()), XyoMultiTypeArrayInt(arrayOf()))

    @kotlin.test.Test
    fun testMultiPartyBoundWitness () {
        runBlocking {
            var currentCacheData : XyoBoundWitnessTransfer? = null
            val boundWitnesses = Array(numberOfSigners, {i -> XyoZigZagBoundWitness(arrayOf(XyoRsaWithSha256.newInstance()), payload) })

            for (i in 0..numberOfSigners - 2) {
                currentCacheData = boundWitnesses[i].incomingData(currentCacheData, false).await().value
            }

            currentCacheData = boundWitnesses[numberOfSigners - 1].incomingData(currentCacheData, true).await().value

            for (i in 0..numberOfSigners - 2) {
                currentCacheData = boundWitnesses[i].incomingData(currentCacheData, false).await().value
            }

            boundWitnesses[numberOfSigners - 1].incomingData(currentCacheData, false).await().value

            for (i in 0..numberOfSigners - 1) {
                printBoundWitness(boundWitnesses[i])
            }
        }
    }
}