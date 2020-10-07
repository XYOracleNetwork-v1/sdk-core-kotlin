package network.xyo.sdkobjectmodelkotlin.structure

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoSchemaException

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class XyoObjectFlags {
    val value: UByte

    constructor(value: UByte) {
        this.value = value
    }

    constructor(sizeId: SizeIdentifier, iterable: Boolean, typed: Boolean) {
        val iterableByte = if (iterable) Masks.Iterable.value else 0x0.toUByte()
        val typedByte = if (iterable) Masks.Typed.value else 0x0.toUByte()
        this.value = sizeId.value.id.rotateLeft(6).or(iterableByte).or(typedByte)
    }

    constructor(size: Int, iterable: Boolean, typed: Boolean) {
        val iterableByte = if (iterable) Masks.Iterable.value else 0x0.toUByte()
        val typedByte = if (iterable) Masks.Typed.value else 0x0.toUByte()
        this.value = sizeIdentifierFromLength(size).value.id.rotateLeft(6).or(iterableByte).or(typedByte)
    }

    companion object {
        enum class Masks(val value: UByte) {
            SizeIdentifier(0xc0.toUByte()),
            Iterable(0x20.toUByte()),
            Typed(0x10.toUByte())
        }

        class SizeIdentifierValue(
                val id: UByte,
                val size: Int
        )

        enum class SizeIdentifier(val value: SizeIdentifierValue) {
            One(SizeIdentifierValue(0x00.toUByte(), 1)),
            Two(SizeIdentifierValue(0x01.toUByte(), 2)),
            Four(SizeIdentifierValue(0x02.toUByte(), 4)),
            Eight(SizeIdentifierValue(0x03.toUByte(), 8))
        }

        fun sizeIdentifierFromValue(value: UByte): SizeIdentifier {
            return when (value) {
                SizeIdentifier.One.value.id -> SizeIdentifier.One
                SizeIdentifier.Two.value.id -> SizeIdentifier.Two
                SizeIdentifier.Four.value.id -> SizeIdentifier.Four
                SizeIdentifier.Eight.value.id -> SizeIdentifier.Eight
                else -> throw XyoSchemaException("Invalid Size: ${value.toString(2)}")
            }
        }

        fun sizeIdentifierFromLength(length: Int): SizeIdentifier {
            if (length <= 0xff) return SizeIdentifier.One
            if (length <= 0xffff) return SizeIdentifier.Two
            if (length <= 0xffffffff) return SizeIdentifier.Four
            return SizeIdentifier.Eight
        }
    }
}