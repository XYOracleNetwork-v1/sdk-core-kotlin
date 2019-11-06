package network.xyo.sdkcorekotlin.network

import network.xyo.sdkobjectmodelkotlin.exceptions.XyoObjectException

/**
 * Helper object to help parse the choice stage of the network protocol.
 * @property bytes array of choice
 */
class XyoChoicePacket (private val bytes: ByteArray) {

    @kotlin.ExperimentalUnsignedTypes
    fun getChoice (): ByteArray {
        val size = getSizeOfChoice()

        if (size + 1 > bytes.size && bytes.isNotEmpty()) {
            throw XyoObjectException("Invalid choice!")
        }

        return bytes.copyOfRange(1, size + 1)
    }

    @kotlin.ExperimentalUnsignedTypes
    fun getResponse (): ByteArray {
        val size = getSizeOfChoice()

        if (size > bytes.size && bytes.isNotEmpty()) {
            throw XyoObjectException("Invalid response!")
        }

        return bytes.copyOfRange(size + 1, bytes.size)
    }

    @kotlin.ExperimentalUnsignedTypes
    private fun getSizeOfChoice (): Int {
        return bytes[0].toUByte().toInt()
    }
}