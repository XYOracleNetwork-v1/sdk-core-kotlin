package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import org.junit.Assert
import org.junit.Test

@ExperimentalUnsignedTypes
class XyoObjectSchemaTest {

    @Test
    fun testEncodingCatalogue () {
        val testSchema = object : XyoObjectSchema() {
            override val id: Byte = 0x13
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 2
            override val meta: XyoObjectSchemaMeta? = null
        }

        val encodingCatalogue = testSchema.encodingCatalogue

        Assert.assertEquals(0x60.toUByte() /* 01100000 */, encodingCatalogue.toUByte())
    }

    @Test
    fun testHeader () {
        val testSchema = object : XyoObjectSchema() {
            override val id: Byte = 0x11
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 2
            override val meta: XyoObjectSchemaMeta? = null
        }

        val encodingCatalogue = testSchema.header

        Assert.assertArrayEquals(ubyteArrayOf(0x60.toUByte() /* 01100000 */, 0x11.toUByte()).toByteArray(), encodingCatalogue)
    }

    @Test
    fun testCreateSchemaFromHeader () {
        val testSchema = object : XyoObjectSchema() {
            override val id: Byte = 0x12
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 2
            override val meta: XyoObjectSchemaMeta? = null
        }

        val recreatedTestSchema = XyoObjectSchema.createFromHeader(testSchema.header)

        Assert.assertArrayEquals(testSchema.header, recreatedTestSchema.header)
    }

    @Test
    fun test1ByteSize () {
        val testHeader = object : XyoObjectSchema() {
            override val id: Byte = 0x12
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 1
            override val meta: XyoObjectSchemaMeta? = null
        }.header

        Assert.assertEquals(1, XyoObjectSchema.createFromHeader(testHeader).sizeIdentifier)
    }

    @Test
    fun test2ByteSize () {
        val testHeader = object : XyoObjectSchema() {
            override val id: Byte = 0x12
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 2
            override val meta: XyoObjectSchemaMeta? = null
        }.header


        Assert.assertEquals(2, XyoObjectSchema.createFromHeader(testHeader).sizeIdentifier)
    }

    @Test
    fun test4ByteSize () {
        val testHeader = object : XyoObjectSchema() {
            override val id: Byte = 0x12
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 4
            override val meta: XyoObjectSchemaMeta? = null
        }.header

        Assert.assertEquals(4, XyoObjectSchema.createFromHeader(testHeader).sizeIdentifier)
    }

    @Test
    fun test8ByteSize () {
        val testHeader = object : XyoObjectSchema() {
            override val id: Byte = 0x12
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val sizeIdentifier: Int = 8
            override val meta: XyoObjectSchemaMeta? = null
        }.header

        println(testHeader.toHexString())

        Assert.assertEquals(8, XyoObjectSchema.createFromHeader(testHeader).sizeIdentifier)
    }

    @Test
    fun testEveryHeader () {
        for (i in 0..255) {
            for (j in 0..255) {
                val schema = XyoObjectSchema.createFromHeader(byteArrayOf(i.toByte(), j.toByte()))

                // 0x0f = 11110000
                // we mask off the bottom 4 bits (reserved bits)
                Assert.assertEquals((i and 0xf0).toByte(), schema.encodingCatalogue)
                Assert.assertEquals(j.toByte(), schema.id)
            }
        }
    }
}