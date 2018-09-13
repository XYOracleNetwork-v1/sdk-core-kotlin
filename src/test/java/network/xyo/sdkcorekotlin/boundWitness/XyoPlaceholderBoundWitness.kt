package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoTestPlaceholder
import network.xyo.sdkcorekotlin.signing.XyoTestSigner

class XyoPlaceholderBoundWitness : XyoTestBase() {
    val boundWitness = XyoZigZagBoundWitness(
            arrayOf(XyoTestSigner()),
            XyoPayload(
                    XyoMultiTypeArrayInt(
                    arrayOf(XyoRssi(-60))
            ),
                    XyoMultiTypeArrayInt(
                    arrayOf(XyoRssi(-60)))
            )

    )

    @kotlin.test.Test
    fun doBoundWitness () {
        runBlocking {
            boundWitness.incomingData(null, true).await()
            printBoundWitness(boundWitness)
            println(bytesToString(boundWitness.typed))
        }
    }
}