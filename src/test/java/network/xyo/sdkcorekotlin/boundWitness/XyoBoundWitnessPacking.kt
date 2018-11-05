package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
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
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class XyoBoundWitnessPacking : XyoTestBase() {
    private val aliceSigners = arrayOf<XyoSigner>(XyoSha256WithSecp256K(null))
    private val aliceSignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-32)))
    private val aliceUnsignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-52)))

    @Test
    fun packAndUnpackBoundWitnessTest () {
        runBlocking {
            XyoKeySet.enable()
            XyoPayload.enable()
            XyoSignatureSet.enable()
            XyoSecp256k1Sha256WithEcdsaSignature.enable()
            XyoRssi.enable()
            XyoRsaWithSha256Signature.enable()
            XyoSecp256K1UnCompressedPublicKey.enable()
            XyoRsaPublicKey.enable()

            val alicePayload = XyoPayload(aliceSignedPayload, aliceUnsignedPayload)
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            val packedBoundWitness = aliceBoundWitness.untyped

            val recreated = XyoBoundWitness.createFromPacked(packedBoundWitness)

            assertArrayEquals(recreated.untyped, packedBoundWitness)
        }
    }
}