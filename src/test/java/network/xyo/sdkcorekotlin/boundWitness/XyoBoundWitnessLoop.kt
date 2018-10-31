package network.xyo.sdkcorekotlin.boundWitness

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoBridgeHashSet
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoBridgeBlockSet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.XyoSha256WithSecp256K
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.keys.XyoSecp256K1UnCompressedPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.ecc.secp256k.signatures.XyoSecp256k1Sha256WithEcdsaSignature
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.XyoRsaPublicKey
import network.xyo.sdkcorekotlin.signing.algorithms.rsa.signatures.XyoRsaWithSha256Signature
import org.junit.Test
import java.math.BigInteger

//class XyoBoundWitnessLoop : XyoTestBase() {
//
//    @Test
//    fun test () {
//        val array = XyoMultiTypeArrayInt(arrayOf(XyoRssi(-34))).untyped
//
//        XyoMultiTypeArrayInt.createFromPacked(array)
//    }
//
//    init {
//        // XyoRssi.enable()
////        XyoKeySet.enable()
////        XyoPayload.enable()
////        XyoSignatureSet.enable()
////        XyoPreviousHash.enable()
////        XyoSha256WithSecp256K.enable()
////        XyoRssi.enable()
////        XyoSecp256K1UnCompressedPublicKey.enable()
////        XyoSecp256k1Sha256WithEcdsaSignature.enable()
////        XyoRsaPublicKey.enable()
////        XyoNextPublicKey.enable()
////        XyoRsaPublicKey.enable()
////        XyoRsaWithSha256Signature.enable()
////        XyoIndex.enable()
////        XyoSha256.enable()
////        XyoSingleTypeArrayInt.enable()
////        XyoBoundWitness.enable()
////        XyoBridgeBlockSet.enable()
////        XyoBridgeHashSet.enable()
//    }
//}