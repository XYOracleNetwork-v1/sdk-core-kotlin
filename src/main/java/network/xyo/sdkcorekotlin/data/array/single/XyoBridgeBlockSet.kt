package network.xyo.sdkcorekotlin.data.array.single

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import network.xyo.sdkcorekotlin.data.array.multi.XyoBridgeHashSet
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayBase
import network.xyo.sdkcorekotlin.hashing.XyoHash
import java.nio.ByteBuffer

/**
 * An array of origin blocks that are being transferred.
 *
 * @major 0x02
 * @minor 0x15
 * @param array The array of origin blocks.
 */

open class XyoBridgeBlockSet(override var array: Array<XyoObject>) : XyoSingleTypeArrayInt(XyoBoundWitness.major, XyoBoundWitness.minor, array) {
    override val id: XyoResult<ByteArray> = XyoResult(byteArrayOf(major, minor))
    override val sizeIdentifierSize: XyoResult<Int?> = sizeOfBytesToGetSize

    /**
     * Gets a XyoBridgeHashSet for this Block set when transferring to a bridge.
     *
     * @param hashProvider The hash creator to hash the blocks with.
     * @return A XyoBridgeHashSet for this block set.
     */
    fun getHashSet (hashProvider: XyoHash.XyoHashProvider) = async {
        val hashes = ArrayList<XyoObject>()
        for (block in array) {
            if (block is XyoBoundWitness) {
                val hash = block.getHash(hashProvider).await().value
                if (hash != null) {
                    hashes.add(hash)
                }
            }
        }
        return@async XyoBridgeHashSet(hashes.toTypedArray())
    }

    companion object : XyoArrayProvider() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x15
        override val sizeOfBytesToGetSize: XyoResult<Int?> = XyoResult(4)

        override fun readSize(byteArray: ByteArray): XyoResult<Int> {
            return XyoResult(XyoUnsignedHelper.readUnsignedInt(byteArray))
        }

        override fun createFromPacked(byteArray: ByteArray): XyoResult<XyoObject> {
            val unpackedArray = XyoArrayDecoder(byteArray, true, 4).array

            if (unpackedArray.error != null) return XyoResult(
                    unpackedArray.error ?: XyoError(
                            this.toString(),
                            "Unknown array unpacking error!"
                    )
            )
            val unpackedArrayValue = unpackedArray.value ?: return XyoResult(XyoError(
                    this.toString(),
                    "Array value is null!"
            ))

            val unpackedArrayObject = XyoBridgeBlockSet(unpackedArrayValue.toTypedArray())
            return XyoResult(unpackedArrayObject)
        }
    }
}