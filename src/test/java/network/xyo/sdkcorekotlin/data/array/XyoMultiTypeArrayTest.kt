package network.xyo.sdkcorekotlin.data.array

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayByte
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayShort
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayByte
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.hashing.basic.XyoMd2
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256
import org.junit.Assert

class XyoMultiTypeArrayTest : XyoTestBase() {
    private val expectedIntSize = byteArrayOf (
            0x00, 0x00, 0x00, 0x1c,         // total size including itself
            XyoRssi.major, XyoRssi.minor,   // major and minor of element type
            0x00,                           // array[0]
            XyoRssi.major, XyoRssi.minor,   // major and minor of element type
            0x01,                           // array[1]
            XyoMd2.major, XyoMd2.minor,     // major and minor of element type
            0x30, 0xBD.toByte(), 0x02, 0x6F,// element [2]
            0x5B, 0x88.toByte(), 0xB4.toByte(),
            0x71, 0x9B.toByte(), 0x56, 0x3B,
            0xDD.toByte(), 0xB6.toByte(),
            0x89.toByte(), 0x17, 0xBE.toByte()
    )

    private val expectedShortSize = byteArrayOf (
            0x00, 0x1a,                     // total size including itself
            XyoRssi.major, XyoRssi.minor,   // major and minor of array type
            0x00,                           // array[0]
            XyoRssi.major, XyoRssi.minor,   // major and minor of element type
            0x01,                           // array[1]
            XyoMd2.major, XyoMd2.minor,     // major and minor of element type
            0x30, 0xBD.toByte(), 0x02, 0x6F,// element [2]
            0x5B, 0x88.toByte(), 0xB4.toByte(),
            0x71, 0x9B.toByte(), 0x56, 0x3B,
            0xDD.toByte(), 0xB6.toByte(),
            0x89.toByte(), 0x17, 0xBE.toByte()
    )

    private val expectedByteSize = byteArrayOf (
            0x19,                           // total size including itself
            XyoRssi.major, XyoRssi.minor,   // major and minor of array type
            0x00,                           // array[0]
            XyoRssi.major, XyoRssi.minor,   // major and minor of element type
            0x01,                           // array[1]
            XyoMd2.major, XyoMd2.minor,     // major and minor of element type
            0x30, 0xBD.toByte(), 0x02, 0x6F,// element [2]
            0x5B, 0x88.toByte(), 0xB4.toByte(),
            0x71, 0x9B.toByte(), 0x56, 0x3B,
            0xDD.toByte(), 0xB6.toByte(),
            0x89.toByte(), 0x17, 0xBE.toByte()
    )

    private val elements = arrayOf(
            XyoRssi(0),
            XyoRssi(1),
            getTestHash()
    )

    @kotlin.test.Test
    fun testIntMultiArray () {
        XyoMd2.enable()
        XyoRssi.enable()
        val intArray = XyoMultiTypeArrayInt(elements)
        Assert.assertArrayEquals(expectedIntSize, intArray.untyped)

        val intArrayCreatedFromBytes = XyoMultiTypeArrayInt.createFromPacked(intArray.untyped)
        Assert.assertArrayEquals(expectedIntSize, intArrayCreatedFromBytes.untyped)
    }

    @kotlin.test.Test
    fun testShortMultiArray () {
        XyoMd2.enable()
        XyoRssi.enable()
        val shortArray = XyoMultiTypeArrayShort(elements)
        Assert.assertArrayEquals(expectedShortSize, shortArray.untyped)

        val intArrayCreatedFromBytes= XyoMultiTypeArrayShort.createFromPacked(shortArray.untyped)
        Assert.assertArrayEquals(expectedShortSize, intArrayCreatedFromBytes.untyped)
    }

    @kotlin.test.Test
    fun testByteMultiArray () {
        XyoMd2.enable()
        XyoRssi.enable()
        val byteArray = XyoMultiTypeArrayByte(elements)
        Assert.assertArrayEquals(expectedByteSize, byteArray.untyped)

        val intArrayCreatedFromBytes = XyoMultiTypeArrayByte.createFromPacked(byteArray.untyped)
        Assert.assertArrayEquals(expectedByteSize, intArrayCreatedFromBytes.untyped)
    }

    private fun getTestHash() : XyoObject = runBlocking {
        return@runBlocking XyoMd2.createHash(byteArrayOf(0x01, 0x02, 0x03)).await()
    }
}