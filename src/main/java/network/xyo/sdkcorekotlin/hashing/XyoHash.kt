package network.xyo.sdkcorekotlin.hashing

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoObjectCreator

abstract class XyoHash : XyoObject() {
    abstract val hash : ByteArray

    override val data: ByteArray
        get() = hash

    fun verifyHash (data : ByteArray) : Boolean {
        val hashCreator = XyoObjectCreator.getCreator(id[0], id[1]) as? XyoHashCreator
        if (hashCreator != null) {
            if (hashCreator.hash(data).contentEquals(hash)) {
                return true
            }
            return false
        }
        throw Exception()
    }

    abstract class XyoHashCreator : XyoObjectCreator() {
        override val major: Byte
            get() = 0x03

        abstract fun hash (data : ByteArray) : ByteArray
        abstract fun createHash (data: ByteArray) : XyoHash
    }
}