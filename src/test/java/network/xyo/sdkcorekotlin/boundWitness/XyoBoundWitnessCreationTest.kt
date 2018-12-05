package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k.XyoEcSecp256K1
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import org.junit.Assert
import org.junit.Test

class XyoBoundWitnessCreationTest : XyoTestBase() {

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            XyoRsaWithSha256.enable()
            XyoSha256WithSecp256K.enable()
            val aliceSigners = arrayOf<XyoSigner>(XyoSha256WithSecp256K.newInstance(), XyoRsaWithSha256.newInstance())
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, arrayOf(), arrayOf())
            aliceBoundWitness.incomingData(null, true).await()

            Assert.assertEquals(1, aliceBoundWitness[XyoSchemas.FETTER.id].size)
            Assert.assertEquals(1, aliceBoundWitness[XyoSchemas.WITNESS.id].size)
            Assert.assertEquals(1, aliceBoundWitness.numberOfParties)
            Assert.assertTrue(aliceBoundWitness.completed)

            Assert.assertTrue(XyoBoundWitnessVerify(false).verify(aliceBoundWitness).await()!!)
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

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false).await()
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne, true).await()
            Assert.assertFalse(boundWitnessAlice.completed)
            Assert.assertFalse(boundWitnessBob.completed)
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne, false).await()
            boundWitnessBob.incomingData(aliceToBobTwo, false).await()

            println(boundWitnessBob)
            println(boundWitnessAlice)

            Assert.assertTrue(XyoBoundWitnessVerify(false).verify(boundWitnessBob).await()!!)

            Assert.assertArrayEquals(boundWitnessAlice.bytesCopy, boundWitnessBob.bytesCopy)
            Assert.assertTrue(boundWitnessAlice.completed)
            Assert.assertTrue(boundWitnessBob.completed)
            Assert.assertEquals(2, boundWitnessAlice.numberOfParties)
            Assert.assertEquals(2, boundWitnessBob.numberOfParties)

            println(boundWitnessAlice.bytesCopy.toHexString())

        }
    }
}