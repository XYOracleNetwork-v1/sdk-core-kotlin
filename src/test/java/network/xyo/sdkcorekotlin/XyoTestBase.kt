package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import org.junit.Assert

open class XyoTestBase {
    fun String.hexStringToByteArray() : ByteArray {
        val hexChars = "0123456789ABCDEF"
        val result = ByteArray(length / 2)

        for (i in 0 until length step 2) {
            val firstIndex = hexChars.indexOf(this[i]);
            val secondIndex = hexChars.indexOf(this[i + 1]);

            val octet = firstIndex.shl(4).or(secondIndex)
            result.set(i.shr(1), octet.toByte())
        }

        return result
    }

    fun bytesToString(bytes: ByteArray?): String {
        val sb = StringBuilder()
        val it = bytes!!.iterator()
        sb.append("0x")
        while (it.hasNext()) {
            sb.append(String.format("%02X ", it.next())) 
        }

        return sb.toString()
    }

    fun assertArrayOfXyoObjects(expected: Array<XyoObject>, actual:  Array<XyoObject>) {
        for (i in 0..expected.size - 1) {
            assertXyoObject(expected[i], actual[i])
        }
    }

    fun assertArrayOfXyoObjects(expected: ArrayList<XyoObject>, actual:  ArrayList<XyoObject>) {
        for (i in 0..expected.size - 1) {
            assertXyoObject(expected[i], actual[i])
        }
    }

    fun assertXyoObject(expected: XyoObject, actual : XyoObject) {
        Assert.assertArrayEquals(expected.data, actual.data)
        Assert.assertArrayEquals(expected.id, actual.id)
        Assert.assertArrayEquals(expected.typed, actual.typed)
        Assert.assertArrayEquals(expected.untyped, actual.untyped)
    }

    fun printBoundWitness(boundWitness : XyoBoundWitness) {
        println("-------------------")
        println("Public Keys")
        for (publicKeySet in boundWitness.publicKeys) {
            println("--Key Set")
            val castedPublicKeySet = publicKeySet as XyoKeySet
            for (publicKey in castedPublicKeySet.array) {
                println("----" + bytesToString(publicKey.typed))
            }
        }

        println("Payloads")
        for (payload in boundWitness.payloads) {
            val castedPayload = payload as XyoPayload
            println("--Payload")
            println("----Signed: " + bytesToString(castedPayload.signedPayload.typed))
            println("----Unsigned: " + bytesToString(castedPayload.unsignedPayload.typed))
        }

        println("Signatures")
        for (signaturesSet in boundWitness.signatures) {
            println("--Signatures Set")
            val castedSignaturesSet = signaturesSet as XyoSignatureSet
            for (signature in castedSignaturesSet.array) {
                println("----" + bytesToString(signature.typed))
            }
        }
    }

    fun assertBoundWitness (boundWitnessOne : XyoBoundWitness, boundWitnessTwo : XyoBoundWitness){
        Assert.assertArrayEquals(boundWitnessOne.typed, boundWitnessTwo.typed)
    }
}