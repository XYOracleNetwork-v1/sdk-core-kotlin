package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha3
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import org.junit.Assert
import org.junit.Test

class XyoTestBoundWitnessUtil : XyoTestBase() {

//    @Test
//    fun testRemoveFromUnsigned () {
//        runBlocking {
//            val aliceSigners = arrayOf<XyoSigner>(XyoRsaWithSha256.newInstance())
//            val aliceSignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf())
//            val aliceUnsignedPayload = XyoObjectSetCreator.createUntypedIterableObject(XyoSchemas.ARRAY_UNTYPED, arrayOf(
//                    XyoObjectCreator.createObject(XyoSchemas.INDEX, byteArrayOf(0x13, 0x37))
//            ))
//
//            val alicePayload = XyoObjectSetCreator.createTypedIterableObject(XyoSchemas.PAYLOAD, arrayOf(aliceSignedPayload, aliceUnsignedPayload))
//            val aliceBoundWitness = XyoZigZagBoundWitness(aliceSigners, alicePayload)
//            aliceBoundWitness.incomingData(null, true).await()
//            val removedItemBoundWitness = XyoBoundWitnessUtil.removeTypeFromUnsignedPayload(XyoSchemas.INDEX.id, aliceBoundWitness.self)
//
//                                                /* todo standardize this XyoObjectIterator(XyoObjectIterator(XyoObjectIterator()[x])[y])[z] */
//
//            Assert.assertEquals(1, XyoIterableObject(XyoIterableObject(XyoIterableObject(XyoIterableObject(aliceBoundWitness.self)[1])[0])[1])[XyoSchemas.INDEX.id].size)
//            Assert.assertEquals(0, XyoIterableObject(XyoIterableObject(XyoIterableObject(XyoIterableObject(removedItemBoundWitness)[1])[0])[1])[XyoSchemas.INDEX.id].size)
//
//            val originalHash = XyoBoundWitness.getInstance(aliceBoundWitness.self).getHash(XyoSha3).await()
//            val removedHash = XyoBoundWitness.getInstance(removedItemBoundWitness).getHash(XyoSha3).await()
//
//            Assert.assertArrayEquals(originalHash.self, removedHash.self)
//        }
//    }
}