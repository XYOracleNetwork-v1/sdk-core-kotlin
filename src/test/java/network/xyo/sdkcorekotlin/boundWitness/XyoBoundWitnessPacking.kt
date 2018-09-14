package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigner
import network.xyo.sdkcorekotlin.signing.XyoTestSigner
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256kSha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import org.junit.Assert

class XyoBoundWitnessPacking : XyoTestBase() {
    private val aliceSigners = arrayOf<XyoSigner>(XyoRsaWithSha256())
    private val aliceSignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-32)))
    private val aliceUnsignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-52)))

    @kotlin.test.Test
    fun packAndUnpackBoundWitnessTest () {
        runBlocking {
            XyoKeySet.enable()
            XyoPayload.enable()
            XyoSignatureSet.enable()
            XyoSecp256kSha256WithEcdsaSignature.enable()
            XyoRssi.enable()
            XyoRsaWithSha256Signature.enable()
            XyoSecp256K1UnCompressedPublicKey.enable()
            XyoRsaPublicKey.enable()

            val alicePayload = XyoPayload(aliceSignedPayload, aliceUnsignedPayload)
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            val packedBoundWitness = aliceBoundWitness.untyped

            val recreated = XyoBoundWitness.createFromPacked(packedBoundWitness)

            Assert.assertArrayEquals(recreated.untyped, packedBoundWitness)
        }
    }
}