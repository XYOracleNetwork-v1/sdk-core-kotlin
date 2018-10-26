package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import org.junit.Assert.assertArrayEquals
import java.security.Security

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

    fun ByteArray.toHexString(): String {
        val builder = StringBuilder()
        val it = this.iterator()
        builder.append("0x")
        while (it.hasNext()) {
            builder.append(String.format("%02X ", it.next()))
        }

        return builder.toString()
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
        assertArrayEquals(expected.objectInBytes, actual.objectInBytes)
        assertArrayEquals(expected.id, actual.id)
        assertArrayEquals(expected.typed, actual.typed)
        assertArrayEquals(expected.untyped, actual.untyped)
    }

    fun printBoundWitness(boundWitness : XyoBoundWitness) {
        println("-------------------")
        println("Public Keys")
        for (publicKeySet in boundWitness.publicKeys) {
            println("--Key Set")
            for (publicKey in publicKeySet.array) {
                println("----" + publicKey.typed.toHexString())
            }
        }

        println("Payloads")
        for (payload in boundWitness.payloads) {
            println("--Payload")
            println("----Signed: " + payload.signedPayload.typed.toHexString())
            println("----Unsigned: " + payload.unsignedPayload.typed.toHexString())
        }

        println("Signatures")
        for (signaturesSet in boundWitness.signatures) {
            println("--Signatures Set")
            for (signature in signaturesSet.array) {
                println("----" + signature.typed.toHexString())
            }
        }
    }

    fun assertBoundWitness (boundWitnessOne : XyoBoundWitness, boundWitnessTwo : XyoBoundWitness){
       assertArrayEquals(boundWitnessOne.objectInBytes, boundWitnessTwo.objectInBytes)
    }
}