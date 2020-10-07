package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectIteratorException
import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
@ExperimentalStdlibApi
class XyoObjectIteratorTest  {

    @Test
    fun testObjectIteratorUntyped () {
        val iterator = XyoIterableStructure(byteArrayOf(0x20, 0x41, 0x09, 0x00, 0x44, 0x02, 0x14, 0x00, 0x42, 0x02, 0x37), 0).iterator
        var index = 0

        while (iterator.hasNext()) {

            if (index == 0) {
                Assert.assertArrayEquals(iterator.next().bytesCopy, byteArrayOf(0x00, 0x44, 0x02, 0x14))
            }

            if (index == 1) {
                Assert.assertArrayEquals(iterator.next().bytesCopy, byteArrayOf(0x00, 0x42, 0x02, 0x37))
            }

            index++
        }
    }

    @Test
    fun testObjectIteratorTyped () {

        val iterator = XyoIterableStructure( byteArrayOf(0x30, 0x41, 0x07, 0x00, 0x44, 0x02, 0x13, 0x02, 0x37), 0).iterator
        var index = 0

        while (iterator.hasNext()) {

            if (index == 0) {
                Assert.assertArrayEquals(iterator.next().bytesCopy, byteArrayOf(0x00, 0x44, 0x02, 0x13))
            }

            if (index == 1) {
                Assert.assertArrayEquals(iterator.next().bytesCopy, byteArrayOf(0x00, 0x44, 0x02, 0x37))
            }

            index++
        }
    }

    @Test
    fun testGetAtIndex () {
        val iterator = XyoIterableStructure(byteArrayOf(0x30, 0x41, 0x07, 0x00, 0x44, 0x02, 0x13, 0x02, 0x37), 0)

        Assert.assertArrayEquals(byteArrayOf(0x00, 0x44, 0x02, 0x13), iterator[0].bytesCopy)
        Assert.assertArrayEquals(byteArrayOf(0x00, 0x44, 0x02, 0x37), iterator[1].bytesCopy)
    }

    @Test
    fun testGetSize () {
        val iterator = XyoIterableStructure(byteArrayOf(0x30, 0x41, 0x07, 0x00, 0x44, 0x02, 0x13, 0x02, 0x37), 0)

        Assert.assertEquals(2, iterator.count)
    }
}