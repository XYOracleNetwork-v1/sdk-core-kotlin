package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256

class XyoTwoPartyBoundWitnessTest : XyoTestBase() {
    private val signersAlice = arrayOf(XyoRsaWithSha256(), XyoSha256WithSecp256K(),XyoRsaWithSha256())
    private val signedPayloadAlice = XyoMultiTypeArrayInt(arrayOf(XyoRssi(5)))
    private val unsignedPayloadAlice = XyoMultiTypeArrayInt(arrayOf(XyoRssi(5)))

    private val signersBob = arrayOf(XyoRsaWithSha256(), XyoSha256WithSecp256K())
    private val signedPayloadBob= XyoMultiTypeArrayInt(arrayOf(XyoRssi(10)))
    private val unsignedPayloadBob= XyoMultiTypeArrayInt(arrayOf(XyoRssi(10)))

    @kotlin.test.Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            val payloadAlice = XyoPayload(signedPayloadAlice, unsignedPayloadAlice)
            val payloadBob = XyoPayload(signedPayloadBob, unsignedPayloadBob)
            val boundWitnessAlice = XyoZigZagBoundWitness(signersAlice, payloadAlice)
            val boundWitnessBob = XyoZigZagBoundWitness(signersBob, payloadBob)

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false).await()
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne.value, true).await()
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne.value, false).await()
            boundWitnessBob.incomingData(aliceToBobTwo.value, false).await()

            assertBoundWitness(boundWitnessAlice, boundWitnessBob)
        }
    }
}