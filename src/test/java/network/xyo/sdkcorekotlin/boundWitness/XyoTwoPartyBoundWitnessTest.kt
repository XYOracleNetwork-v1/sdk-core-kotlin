package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkobjectmodelkotlin.objects.sets.XyoObjectSetCreator
import org.junit.Assert
import org.junit.Test

class XyoTwoPartyBoundWitnessTest : XyoTestBase() {
    private val signersAlice = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
    private val signedPayloadAlice = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY, arrayOf())
    private val unsignedPayloadAlice = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY, arrayOf())

    private val signersBob = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
    private val signedPayloadBob = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY, arrayOf())
    private val unsignedPayloadBob= XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY, arrayOf())

    @Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            val payloadAlice = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(signedPayloadAlice, unsignedPayloadAlice))
            val payloadBob = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(signedPayloadBob, unsignedPayloadBob))
            val boundWitnessAlice = XyoZigZagBoundWitness(signersAlice, payloadAlice)
            val boundWitnessBob = XyoZigZagBoundWitness(signersBob, payloadBob)

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false).await()
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne, true).await()
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne, false).await()
            boundWitnessBob.incomingData(aliceToBobTwo, false).await()

//            assertBoundWitness(boundWitnessAlice, boundWitnessBob)
//
//            val packedBoundWitness = boundWitnessAlice.untyped
//            val recreated = XyoBoundWitness.createFromPacked(packedBoundWitness)
//
//            Assert.assertArrayEquals(packedBoundWitness, recreated.untyped)
//            Assert.assertEquals(true, XyoBoundWitnessVerify(false).verify(boundWitnessAlice).await())
        }
    }
}