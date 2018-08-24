package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
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

    private val elements = arrayOf(
            XyoRssi(0),
            XyoRssi(1),
            XyoRssi(2)
    )

    @kotlin.test.Test
    fun testIntSingleArray () {
        XyoRssi.enable()
        val intArray = XyoSingleTypeArrayInt(XyoRssi.major, XyoRssi.minor)
        intArray.addElement(elements[0])
        intArray.addElement(elements[1])
        intArray.addElement(elements[2])
        Assert.assertArrayEquals(expectedIntSize, intArray.untyped)

        val intArrayCreatedFromBytes = XyoSingleTypeArrayInt.createFromPacked(intArray.typed)
        assertArrayOfXyoObjects(intArray.array, intArrayCreatedFromBytes.array)
    }

    @kotlin.test.Test
    fun testShortSingleArray () {
        XyoRssi.enable()
        val shortArray = XyoSingleTypeArrayShort(XyoRssi.major, XyoRssi.minor)
        shortArray.addElement(elements[0])
        shortArray.addElement(elements[1])
        shortArray.addElement(elements[2])
        Assert.assertArrayEquals(expectedShortSize, shortArray.untyped)

        val intArrayCreatedFromBytes= XyoSingleTypeArrayShort.createFromPacked(shortArray.typed)
        assertArrayOfXyoObjects(shortArray.array, intArrayCreatedFromBytes.array)
    }

    @kotlin.test.Test
    fun testByteSingleArray () {
        XyoRssi.enable()
        val byteArray = XyoSingleTypeArrayByte(XyoRssi.major, XyoRssi.minor)
        byteArray.addElement(elements[0])
        byteArray.addElement(elements[1])
        byteArray.addElement(elements[2])
        Assert.assertArrayEquals(expectedByteSize, byteArray.untyped)

        val intArrayCreatedFromBytes = XyoSingleTypeArrayByte.createFromPacked(byteArray.typed)
        assertArrayOfXyoObjects(byteArray.array, intArrayCreatedFromBytes.array)
    }
}