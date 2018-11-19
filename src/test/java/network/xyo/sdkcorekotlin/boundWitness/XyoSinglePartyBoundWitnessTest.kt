package network.xyo.sdkcorekotlin.boundWitness

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectIterator
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import org.junit.Assert
import org.junit.Test

class XyoSinglePartyBoundWitnessTest : XyoTestBase() {
    private val aliceSigners = arrayOf<XyoSigner>(XyoRsaWithSha256.newInstance(), XyoRsaWithSha256.newInstance())
    private val aliceSignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
    private val aliceUnsignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            val alicePayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(aliceSignedPayload, aliceUnsignedPayload))
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            assertEquals(1, XyoObjectIterator(aliceBoundWitness.payloads).size)
            assertEquals(1, XyoObjectIterator(aliceBoundWitness.publicKeys).size)
            assertEquals(1, XyoObjectIterator(aliceBoundWitness.signatures).size)
            Assert.assertTrue(aliceBoundWitness.completed)
            Assert.assertEquals(1, XyoBoundWitness.getNumberOfParties(aliceBoundWitness))
        }
    }
}