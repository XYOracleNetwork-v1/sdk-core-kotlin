package network.xyo.sdkobjectmodelkotlin

import network.xyo.sdkobjectmodelkotlin.structure.XyoIterableStructure
import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class XyoObjectSetCreatorTest {
    private val objectOneSchema = XyoObjectSchema(
            0x44,
            isIterable = false,
            isTyped = false,
            sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.One,
            meta = null)

    private val objectTwoSchema = XyoObjectSchema(
            0x42,
            isIterable = false,
            isTyped = false,
            sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.One,
            meta = null)

    @Test
    fun testCreateUntypedSet () {
        val setSchema = XyoObjectSchema(
            0x41,
            isIterable = true,
            isTyped = false,
            sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.One,
            meta = null)

        val objectOne = XyoObjectStructure.newInstance(objectOneSchema, byteArrayOf(0x13))
        val objectTwo =  XyoObjectStructure.newInstance(objectTwoSchema, byteArrayOf(0x37))
        val expectedSet = byteArrayOf(0x20, 0x41, 0x09, 0x00, 0x44, 0x02, 0x13, 0x00, 0x42, 0x02, 0x37)
        val createdSet = XyoIterableStructure.createUntypedIterableObject(setSchema, arrayOf(objectOne, objectTwo))

        Assert.assertArrayEquals(expectedSet, createdSet.bytesCopy)
    }

    @Test
    fun testCreateTypedSet () {
        val setSchema = XyoObjectSchema(
                0x41,
                isIterable = true,
                isTyped = true,
                sizeIdentifier = XyoObjectSchema.Companion.SizeIdentifier.One,
                meta = null)

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