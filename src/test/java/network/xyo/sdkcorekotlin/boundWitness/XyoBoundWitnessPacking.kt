package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1CompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey
import org.junit.Assert

class XyoBoundWitnessPacking : XyoTestBase() {
    private val aliceSigners = arrayOf(XyoSha256WithSecp256K.newInstance().value!!)
    private val aliceSignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-32)))
    private val aliceUnsignedPayload = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-52)))

    @kotlin.test.Test
    fun packAndUnpackBoundWitnessTest () {
        runBlocking {
            XyoKeySet.enable()
            XyoPayload.enable()
            XyoSignatureSet.enable()
            XyoSha256WithEcdsaSignature.enable()
            XyoRssi.enable()
            XyoSecp256K1CompressedPublicKey.enable()
            XyoRsaPublicKey.enable()

            val alicePayload = XyoPayload(aliceSignedPayload, aliceUnsignedPayload)
            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
            aliceBoundWitness.incomingData(null, true).await()

            val packedBoundWitness = aliceBoundWitness.untyped
            if (packedBoundWitness.error != null) throw Exception("packedBoundWitness Error!")
            val packedBoundWitnessValue = packedBoundWitness.value ?: throw Exception("Value is null!")

            val recreated = XyoBoundWitness.createFromPacked(packedBoundWitnessValue)
            Assert.assertArrayEquals(recreated.value!!.untyped.value!!, packedBoundWitness.value!!)
        }
    }
}