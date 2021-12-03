package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.ecdsa.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.crypto.signing.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import org.junit.Assert
import org.junit.Test

class XyoBoundWitnessCreationTest : XyoTestBase() {

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            XyoRsaWithSha256.enable()
            XyoSha256WithSecp256K.enable()
            val aliceSigners = arrayOf(XyoSha256WithSecp256K.newInstance(), XyoRsaWithSha256.newInstance())
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, arrayOf(), arrayOf())
            aliceBoundWitness.incomingData(null, true)

            Assert.assertEquals(1, aliceBoundWitness[XyoSchemas.FETTER.id].size)
            Assert.assertEquals(1, aliceBoundWitness[XyoSchemas.WITNESS.id].size)
            Assert.assertEquals(1, aliceBoundWitness.numberOfParties)
            Assert.assertTrue(aliceBoundWitness.completed)

            Assert.assertTrue(XyoBoundWitnessVerify(false).verify(aliceBoundWitness)!!)
        }
    }

    @Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            XyoSha256WithSecp256K.enable()
            val signersAlice = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
            val signersBob = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))

            val boundWitnessAlice = XyoZigZagBoundWitness(signersAlice, arrayOf(), arrayOf())
            val boundWitnessBob = XyoZigZagBoundWitness(signersBob, arrayOf(), arrayOf())

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false)
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne, true)
            Assert.assertFalse(boundWitnessAlice.completed)
            Assert.assertFalse(boundWitnessBob.completed)
            Assert.assertNull(boundWitnessBob.getBoundWitnessItemAtIndex(0))
            Assert.assertFalse(XyoBoundWitnessVerify(false).verify(boundWitnessBob)!!)
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne, false)
            boundWitnessBob.incomingData(aliceToBobTwo, false)

            Assert.assertTrue(XyoBoundWitnessVerify(false).verify(boundWitnessBob)!!)

            Assert.assertArrayEquals(boundWitnessAlice.bytesCopy, boundWitnessBob.bytesCopy)
            Assert.assertTrue(boundWitnessAlice.completed)
            Assert.assertTrue(boundWitnessBob.completed)
            Assert.assertEquals(2, boundWitnessAlice.numberOfParties)
            Assert.assertEquals(2, boundWitnessBob.numberOfParties)
        }
    }
}