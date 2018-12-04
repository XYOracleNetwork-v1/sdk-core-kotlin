package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import org.junit.Assert
import org.junit.Test

class XyoBoundWitnessCreationTest : XyoTestBase() {

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            val aliceSigners = arrayOf<XyoSigner>(XyoRsaWithSha256.newInstance(), XyoRsaWithSha256.newInstance())
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, arrayOf(), arrayOf())
            aliceBoundWitness.incomingData(null, true).await()

            Assert.assertEquals(1, aliceBoundWitness[XyoSchemas.FETTER.id].size)
            Assert.assertEquals(1, aliceBoundWitness[XyoSchemas.WITNESSS.id].size)
            Assert.assertEquals(1, XyoBoundWitness.getNumberOfParties(aliceBoundWitness))
            Assert.assertTrue(aliceBoundWitness.completed)
        }
    }

    @Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            val signersAlice = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
            val signersBob = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))

            val boundWitnessAlice = XyoZigZagBoundWitness(signersAlice, arrayOf(), arrayOf())
            val boundWitnessBob = XyoZigZagBoundWitness(signersBob, arrayOf(), arrayOf())

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false).await()
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne, true).await()
            Assert.assertFalse(boundWitnessAlice.completed)
            Assert.assertFalse(boundWitnessBob.completed)
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne, false).await()
            boundWitnessBob.incomingData(aliceToBobTwo, false).await()


            Assert.assertArrayEquals(boundWitnessAlice.valueCopy, boundWitnessBob.valueCopy)
            Assert.assertTrue(boundWitnessAlice.completed)
            Assert.assertTrue(boundWitnessBob.completed)
            Assert.assertEquals(2, XyoBoundWitness.getNumberOfParties(boundWitnessAlice))
            Assert.assertEquals(2, XyoBoundWitness.getNumberOfParties(boundWitnessBob))
        }
    }
}