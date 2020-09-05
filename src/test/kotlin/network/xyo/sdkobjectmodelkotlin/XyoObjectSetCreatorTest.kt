package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.junit.Assert
import org.junit.Test

class XyoObjectSetCreatorTest {
    private val objectOneSchema = object : XyoObjectSchema() {
        override val id: Byte = 0x44
        override val isIterable: Boolean = false
        override val isTyped: Boolean = false
        override val meta: XyoObjectSchemaMeta? = null
        override val sizeIdentifier: Int = 1
    }

    private val objectTwoSchema = object : XyoObjectSchema() {
        override val id: Byte = 0x42
        override val isIterable: Boolean = false
        override val isTyped: Boolean = false
        override val meta: XyoObjectSchemaMeta? = null
        override val sizeIdentifier: Int = 1
    }

    @Test
    fun testCreateUntypedSet () {
        val setSchema = object : XyoObjectSchema() {
            override val id: Byte = 0x41
            override val isIterable: Boolean = true
            override val isTyped: Boolean = false
            override val meta: XyoObjectSchemaMeta? = null
            override val sizeIdentifier: Int = 1
        }

        val objectOne = XyoObjectStructure.newInstance(objectOneSchema, byteArrayOf(0x13))
        val objectTwo =  XyoObjectStructure.newInstance(objectTwoSchema, byteArrayOf(0x37))
        val expectedSet = byteArrayOf(0x20, 0x41, 0x09, 0x00, 0x44, 0x02, 0x13, 0x00, 0x42, 0x02, 0x37)
        val createdSet = XyoIterableStructure.createUntypedIterableObject(setSchema, arrayOf(objectOne, objectTwo))

        Assert.assertArrayEquals(expectedSet, createdSet.bytesCopy)
    }

    @Test
    fun testCreateTypedSet () {
        val setSchema = object : XyoObjectSchema() {
            override val id: Byte = 0x41
            override val isIterable: Boolean = true
            override val isTyped: Boolean = true
            override val meta: XyoObjectSchemaMeta? = null
            override val sizeIdentifier: Int = 1
        }

        val objectOne = XyoObjectStructure.newInstance(objectOneSchema, byteArrayOf(0x13))
        val objectTwo =  XyoObjectStructure.newInstance(objectOneSchema, byteArrayOf(0x37))
        val expectedSet = byteArrayOf(
                0x30, 0x41,
                0x07,
                0x00, 0x44,
                0x02, 0x13,
                0x02, 0x37
        )

        val createdSet = XyoIterableStructure.createTypedIterableObject(setSchema, arrayOf(objectOne, objectTwo))

        Assert.assertArrayEquals(expectedSet, createdSet.bytesCopy)
    }
}