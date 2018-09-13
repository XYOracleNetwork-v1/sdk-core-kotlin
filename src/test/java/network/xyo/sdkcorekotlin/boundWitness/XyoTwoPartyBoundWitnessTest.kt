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
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256kSha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature

class XyoTwoPartyBoundWitnessTest : XyoTestBase() {
    private val signersAlice = arrayOf<XyoSigner>(XyoRsaWithSha256(), XyoRsaWithSha256(),XyoSha256WithSecp256K())
    private val signedPayloadAlice = XyoMultiTypeArrayInt(arrayOf(XyoRssi(5)))
    private val unsignedPayloadAlice = XyoMultiTypeArrayInt(arrayOf(XyoRssi(5)))

    private val signersBob = arrayOf<XyoSigner>(XyoRsaWithSha256())
    private val signedPayloadBob= XyoMultiTypeArrayInt(arrayOf(XyoRssi(10)))
    private val unsignedPayloadBob= XyoMultiTypeArrayInt(arrayOf(XyoRssi(10)))

    @kotlin.test.Test
    fun testTwoPartyBoundWitness () {
        runBlocking {
            XyoKeySet.enable()
            XyoPayload.enable()
            XyoRsaPublicKey.enable()
            XyoRsaWithSha256Signature.enable()
            XyoSignatureSet.enable()
            XyoSecp256kSha256WithEcdsaSignature.enable()
            XyoRssi.enable()
            XyoSecp256K1UnCompressedPublicKey.enable()
            XyoRsaPublicKey.enable()

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
            println(bytesToString(packedBoundWitness))
            val recreated = XyoBoundWitness.createFromPacked(packedBoundWitness)
        }
    }
}