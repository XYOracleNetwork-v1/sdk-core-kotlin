package network.xyo.sdkcorekotlin.boundWitness

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import org.junit.Assert
import org.junit.Test

class XyoSinglePartyBoundWitnessTest : XyoTestBase() {
    private val aliceSigners = arrayOf(XyoSha256WithSecp256K.newInstance(), XyoRsaWithSha256.newInstance())
    private val aliceSignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY, arrayOf())
    private val aliceUnsignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY, arrayOf())

    @Test
    fun testSinglePartyBoundWitness () {
        runBlocking {
            XyoSha256WithSecp256K.enable()
            XyoRsaWithSha256.enable()
            val alicePayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(aliceSignedPayload, aliceUnsignedPayload))
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            assertEquals(1, aliceBoundWitness.payloads.size)
            assertEquals(1, aliceBoundWitness.publicKeys.size)
            assertEquals(1, aliceBoundWitness.signatures.size)
            Assert.assertEquals(true, XyoBoundWitnessVerify(false).verify(aliceBoundWitness).await())
        }
    }
}