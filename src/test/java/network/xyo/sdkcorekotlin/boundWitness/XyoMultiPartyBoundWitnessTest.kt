package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import org.junit.Test

class XyoMultiPartyBoundWitnessTest : XyoTestBase() {
    private val numberOfSigners = 3
    private val payload = XyoPayload(XyoMultiTypeArrayInt(arrayOf()), XyoMultiTypeArrayInt(arrayOf()))

    @Test
    fun testMultiPartyBoundWitness () {
        runBlocking {
            var currentCacheData : XyoBoundWitnessTransfer? = null
            val boundWitnesses = Array(numberOfSigners, {i -> XyoZigZagBoundWitness(arrayOf(XyoRsaWithSha256.newInstance()), payload) })

            for (i in 0..numberOfSigners - 2) {
                currentCacheData = boundWitnesses[i].incomingData(currentCacheData, false).await()
            }

            currentCacheData = boundWitnesses[numberOfSigners - 1].incomingData(currentCacheData, true).await()

            for (i in 0..numberOfSigners - 2) {
                currentCacheData = boundWitnesses[i].incomingData(currentCacheData, false).await()
            }

            boundWitnesses[numberOfSigners - 1].incomingData(currentCacheData, false).await()

            for (i in 0..numberOfSigners - 1) {
                printBoundWitness(boundWitnesses[i])
            }
        }
    }
}