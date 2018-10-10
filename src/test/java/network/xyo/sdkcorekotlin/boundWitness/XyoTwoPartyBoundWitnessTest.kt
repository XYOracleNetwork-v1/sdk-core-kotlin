package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import org.junit.Assert
import org.junit.Test

class XyoTwoPartyBoundWitnessTest : XyoTestBase() {
    private val signersAlice = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
    private val signedPayloadAlice = XyoMultiTypeArrayInt(arrayOf(XyoRssi(5)))
    private val unsignedPayloadAlice = XyoMultiTypeArrayInt(arrayOf(XyoRssi(5)))

    private val signersBob = arrayOf<XyoSigner>(XyoRsaWithSha256(null))
    private val signedPayloadBob= XyoMultiTypeArrayInt(arrayOf(XyoRssi(10)))
    private val unsignedPayloadBob= XyoMultiTypeArrayInt(arrayOf(XyoRssi(10)))

    @Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            XyoKeySet.enable()
            XyoPayload.enable()
            XyoRsaPublicKey.enable()
            XyoRsaWithSha256Signature.enable()
            XyoSignatureSet.enable()
            XyoSecp256k1Sha256WithEcdsaSignature.enable()
            XyoRssi.enable()
            XyoSecp256K1UnCompressedPublicKey.enable()
            XyoRsaPublicKey.enable()
            XyoSha256WithSecp256K.enable()
            XyoRsaWithSha256.enable()

            val payloadAlice = XyoPayload(signedPayloadAlice, unsignedPayloadAlice)
            val payloadBob = XyoPayload(signedPayloadBob, unsignedPayloadBob)
            val boundWitnessAlice = XyoZigZagBoundWitness(signersAlice, payloadAlice)
            val boundWitnessBob = XyoZigZagBoundWitness(signersBob, payloadBob)

            val aliceToBobOne = boundWitnessAlice.incomingData(null, false).await()
            val bobToAliceOne = boundWitnessBob.incomingData(aliceToBobOne, true).await()
            val aliceToBobTwo = boundWitnessAlice.incomingData(bobToAliceOne, false).await()
            boundWitnessBob.incomingData(aliceToBobTwo, false).await()

            assertBoundWitness(boundWitnessAlice, boundWitnessBob)

            val packedBoundWitness = boundWitnessAlice.untyped
            val recreated = XyoBoundWitness.createFromPacked(packedBoundWitness)

            Assert.assertArrayEquals(packedBoundWitness, recreated.untyped)

            Assert.assertEquals(true, XyoBoundWitness.verify(boundWitnessAlice).await())
        }
    }
}