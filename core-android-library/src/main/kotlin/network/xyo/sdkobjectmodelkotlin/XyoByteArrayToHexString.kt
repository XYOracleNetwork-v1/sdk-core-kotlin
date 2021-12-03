package network.xyo.sdkobjectmodelkotlin

import java.lang.StringBuilder

/**
 * A function to encode ByteArrays into strings.
 *
 * [0x13, 0x37] -> 0x1337
 *
 * @return A string in base 16 (hex) of the ByteArray. All uppercase.
 */
fun ByteArray.toHexString(): String {
    val builder = StringBuilder()
    val it = this.iterator()
    builder.append("0x")
    while (it.hasNext()) {
        builder.append(String.format("%02X", it.next()))
    }

    return builder.toString()
}