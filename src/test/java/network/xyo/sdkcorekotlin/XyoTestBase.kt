package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import org.junit.Assert.assertArrayEquals

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
}