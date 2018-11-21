package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectIterator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import org.junit.Assert
import org.junit.Test

class XyoBoundWitnessCreationTest : XyoTestBase() {

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            val aliceSigners = arrayOf<XyoSigner>(XyoRsaWithSha256.newInstance(), XyoRsaWithSha256.newInstance())
            val aliceSignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
            val aliceUnsignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())

            val alicePayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(aliceSignedPayload, aliceUnsignedPayload))
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            Assert.assertEquals(1, XyoObjectIterator(aliceBoundWitness.payloads).size)
            Assert.assertEquals(1, XyoObjectIterator(aliceBoundWitness.publicKeys).size)
            Assert.assertEquals(1, XyoObjectIterator(aliceBoundWitness.signatures).size)
            Assert.assertTrue(aliceBoundWitness.completed)
            Assert.assertEquals(1, XyoBoundWitness.getNumberOfParties(aliceBoundWitness))
        }
    }

    @Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            val signersAlice = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
            val signedPayloadAlice = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
            val unsignedPayloadAlice = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
            val signersBob = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
            val signedPayloadBob = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
            val unsignedPayloadBob= XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())

            val payloadAlice = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(signedPayloadAlice, unsignedPayloadAlice))
            val payloadBob = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(signedPayloadBob, unsignedPayloadBob))
            val boundWitnessAlice = XyoZigZagBoundWitness(signersAlice, payloadAlice)
            val boundWitnessBob = XyoZigZagBoundWitness(signersBob, payloadBob)

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false).await()
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne, true).await()
            Assert.assertFalse(boundWitnessAlice.completed)
            Assert.assertFalse(boundWitnessBob.completed)
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne, false).await()
            boundWitnessBob.incomingData(aliceToBobTwo, false).await()

            Assert.assertArrayEquals(boundWitnessAlice.self, boundWitnessBob.self)
            Assert.assertTrue(boundWitnessAlice.completed)
            Assert.assertTrue(boundWitnessBob.completed)
            Assert.assertEquals(2, XyoBoundWitness.getNumberOfParties(boundWitnessAlice))
            Assert.assertEquals(2, XyoBoundWitness.getNumberOfParties(boundWitnessBob))
        }
    }
}