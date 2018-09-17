package network.xyo.sdkcorekotlin.boundWitness

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import org.junit.Test

class XyoSinglePartyBoundWitnessTest : XyoTestBase() {
    private val aliceSigners = arrayOf(XyoSha256WithSecp256K.newInstance(), XyoRsaWithSha256.newInstance())
    private val aliceSignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-32)))
    private val aliceUnsignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-52)))

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            val alicePayload = XyoPayload(aliceSignedPayload, aliceUnsignedPayload)
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            assertEquals(1, aliceBoundWitness.payloads.size)
            assertEquals(1, aliceBoundWitness.publicKeys.size)
            assertEquals(1, aliceBoundWitness.signatures.size)
        }
    }
}