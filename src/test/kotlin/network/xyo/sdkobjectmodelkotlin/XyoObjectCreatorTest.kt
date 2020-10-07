package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.structure.XyoNumberEncoder
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.junit.Assert
import org.junit.Test
@ExperimentalStdlibApi
class XyoObjectCreatorTest {


    @Test
    fun testSmartSizeForByte () {
        val sizeOfImageryObject = 254
        val bestWayToEncodeSize = 1
        Assert.assertEquals(bestWayToEncodeSize, XyoNumberEncoder.getSmartSize(sizeOfImageryObject))
    }

    @Test
    fun testSmartSizeForShort () {
        val sizeOfImageryObject = 64_000
        val bestWayToEncodeSize = 2
        Assert.assertEquals(bestWayToEncodeSize, XyoNumberEncoder.getSmartSize(sizeOfImageryObject))
    }

    @Test
    fun testSmartSizeForInt () {
        val sizeOfImageryObject = 66_000
        val bestWayToEncodeSize = 4
        Assert.assertEquals(bestWayToEncodeSize, XyoNumberEncoder.getSmartSize(sizeOfImageryObject))
    }

    @Test
    fun testCreateObject() {
        val schema = XyoObjectSchema(1,0x44)

        val value = byteArrayOf(0x13)
        val expectedObject = byteArrayOf(0x00, 0x44, 0x02, 0x13)
        val createdObject = XyoObjectStructure.getObjectEncoded(schema, value)

        Assert.assertArrayEquals(expectedObject, createdObject)
    }

    @Test
    fun getObjectValueTest () {
        val testObject = byteArrayOf(0x00, 0x44, 0x02, 0x13)
        XyoObjectStructure(testObject, 0)
        Assert.assertArrayEquals(byteArrayOf(0x13), XyoObjectStructure(testObject, 0).valueCopy)
    }
}