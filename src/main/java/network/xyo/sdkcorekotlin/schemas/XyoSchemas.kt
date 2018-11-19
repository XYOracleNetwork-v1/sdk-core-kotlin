package network.xyo.sdkcorekotlin.schemas

import network.xyo.sdkobjectmodelkotlin.schema.XyoObjectSchema

@ExperimentalUnsignedTypes
object XyoSchemas {
    val BW : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x02

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val ARRAY_TYPED : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x01

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = true

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val ARRAY_UNTYPED : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x01

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val SIGNATURE_SET : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x03

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val KEY_SET : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x04

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val MD2 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x05

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val MD5 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x06

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val SHA1 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x07

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val SHA224 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x08

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val SHA256 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x09

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val SHA384 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x0a

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val SHA512 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x0b

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val SHA3 : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x0c

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val PREVIOUS_HASH : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x0d

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val BRIDGE_BLOCK_SET : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x0e

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val BRIDGE_HASH_SET : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x0f

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val UNIX_TIME : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x10

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val NEXT_PUBLIC_KEY : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x11

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val EC_PUBLIC_KEY : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x12

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val EC_PRIVATE_KEY : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x13

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val EC_SIGNATURE : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x14

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }

    val RSA_PRIVATE_KEY : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x15

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val RSA_PUBLIC_KEY : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x16

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val RSA_SIGNATURE : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x17

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val PAYLOAD : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x18

        override val isIterable: Boolean
            get() = true

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }


    val INDEX : XyoObjectSchema = object : XyoObjectSchema() {
        override val id: Byte
            get() = 0x19

        override val isIterable: Boolean
            get() = false

        override val isTyped: Boolean
            get() = false

        override val meta: XyoObjectSchemaMeta?
            get() = null

        override val sizeIdentifier: Int
            get() = 4
    }
}