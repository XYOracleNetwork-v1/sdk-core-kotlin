package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import org.junit.Assert
import org.junit.Test

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class XyoObjectSchemaTest {

    @Test
    fun testEncodingCatalogue () {
        val testSchema = XyoObjectSchema(
                0x13,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.Two,
                meta = null)

        val encodingCatalogue = testSchema.encodingCatalogue

        Assert.assertEquals(0x60.toUByte() /* 01100000 */, encodingCatalogue.toUByte())
    }

    @Test
    fun testHeader () {
        val testSchema = XyoObjectSchema(
                0x11,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.Two,
                meta = null)

        val encodingCatalogue = testSchema.header

        Assert.assertArrayEquals(ubyteArrayOf(0x60.toUByte() /* 01100000 */, 0x11.toUByte()).toByteArray(), encodingCatalogue)
    }

    @Test
    fun testCreateSchemaFromHeader () {
        val testSchema = XyoObjectSchema(
                0x12,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.Two,
                meta = null)

        val recreatedTestSchema = XyoObjectSchema(testSchema.header)

        Assert.assertArrayEquals(testSchema.header, recreatedTestSchema.header)
    }

    @Test
    fun test1ByteSize () {
        val testHeader = XyoObjectSchema(
                0x12,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.One,
                meta = null).header

        Assert.assertEquals(1, XyoObjectSchema(testHeader).sizeIdentifier.value.size)
    }

    @Test
    fun test2ByteSize () {
        val testHeader = XyoObjectSchema(
                0x12,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.Two,
                meta = null).header

        Assert.assertEquals(2, XyoObjectSchema(testHeader).sizeIdentifier.value.size)
    }

    @Test
    fun test4ByteSize () {
        val testHeader = XyoObjectSchema(
                0x12,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.Four,
                meta = null).header

        Assert.assertEquals(4, XyoObjectSchema(testHeader).sizeIdentifier.value.size)
    }

    @Test
    fun test8ByteSize () {
        val testHeader = XyoObjectSchema(
                0x12,
                isIterable = true,
                isTyped = false,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.Eight,
                meta = null).header

        println(testHeader.toHexString())

        Assert.assertEquals(8, XyoObjectSchema(testHeader).sizeIdentifier.value.size)
    }

    @Test
    fun testEveryHeader () {
        for (i in 0..255) {
            for (j in 0..255) {
                val schema = XyoObjectSchema(byteArrayOf(i.toByte(), j.toByte()))

                // 0x0f = 11110000
                // we mask off the bottom 4 bits (reserved bits)
                Assert.assertEquals((i and 0xf0).toByte(), schema.encodingCatalogue)
                Assert.assertEquals(j.toByte(), schema.id)
            }
        }
    }
}