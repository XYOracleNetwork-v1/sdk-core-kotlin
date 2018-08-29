package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayByte
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import org.junit.Assert

class XyoSingleTypeArrayTest : XyoTestBase() {
    private val expectedIntSize = byteArrayOf (
            0x00, 0x00, 0x00, 0x09,         // total size including itself
            XyoRssi.major, XyoRssi.minor,   // major and minor of element type
            0x00,                           // array[0]
            0x01,                           // array[1]
            0x02                            // array[2]
    )

    private val expectedShortSize = byteArrayOf (
            0x00, 0x07,                     // total size including itself
            XyoRssi.major, XyoRssi.minor,   // major and minor of array type
            0x00,                           // array[0]
            0x01,                           // array[1]
            0x02                            // array[2]
    )

    private val expectedByteSize = byteArrayOf (
            0x06,                           // total size including itself
            XyoRssi.major, XyoRssi.minor,   // major and minor of array type
            0x00,                           // array[0]
            0x01,                           // array[1]
            0x02                            // array[2]
    )

    private val elements = arrayOf<XyoObject>(
            XyoRssi(0),
            XyoRssi(1),
            XyoRssi(2)
    )

    @kotlin.test.Test
    fun testIntSingleArray () {
        XyoRssi.enable()
        val intArray = XyoSingleTypeArrayInt(XyoRssi.major, XyoRssi.minor, elements)
        Assert.assertArrayEquals(expectedIntSize, intArray.untyped.value!!)

        val intArrayCreatedFromBytes = XyoSingleTypeArrayInt.createFromPacked(intArray.untyped.value!!)
        Assert.assertArrayEquals(expectedIntSize, intArrayCreatedFromBytes.value!!.untyped.value)
    }

    @kotlin.test.Test
    fun testShortSingleArray () {
        XyoRssi.enable()
        val shortArray = XyoSingleTypeArrayShort(XyoRssi.major, XyoRssi.minor, elements)
        Assert.assertArrayEquals(expectedShortSize, shortArray.untyped.value!!)

        val intArrayCreatedFromBytes= XyoSingleTypeArrayShort.createFromPacked(shortArray.untyped.value!!)
        Assert.assertArrayEquals(expectedShortSize, intArrayCreatedFromBytes.value!!.untyped.value)
    }

    @kotlin.test.Test
    fun testByteSingleArray () {
        XyoRssi.enable()
        val byteArray = XyoSingleTypeArrayByte(XyoRssi.major, XyoRssi.minor, elements)
        Assert.assertArrayEquals(expectedByteSize, byteArray.untyped.value!!)

        val intArrayCreatedFromBytes = XyoSingleTypeArrayByte.createFromPacked(byteArray.untyped.value!!)
        Assert.assertArrayEquals(expectedByteSize, intArrayCreatedFromBytes.value!!.untyped.value)
    }
}