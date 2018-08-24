package network.xyo.sdkcorekotlin

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
            sb.append(String.format("%02X", it.next()))
        }

        return sb.toString()
    }
}