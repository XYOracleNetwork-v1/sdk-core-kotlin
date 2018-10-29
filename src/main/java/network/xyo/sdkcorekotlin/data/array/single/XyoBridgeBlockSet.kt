package network.xyo.sdkcorekotlin.data.array.single

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoUnsignedHelper
import network.xyo.sdkcorekotlin.data.array.XyoArrayDecoder
import network.xyo.sdkcorekotlin.data.array.multi.XyoBridgeHashSet
import network.xyo.sdkcorekotlin.hashing.XyoHash

/**
 * An array of origin blocks that are being transferred.
 *
 * @major 0x02
 * @minor 0x09
 * @param array The array of origin blocks.
 */

open class XyoBridgeBlockSet(override var array: Array<XyoObject>) : XyoSingleTypeArrayInt(XyoBoundWitness.major, XyoBoundWitness.minor, array) {
    override val id: ByteArray = byteArrayOf(major, minor)
    override val sizeIdentifierSize: Int? = sizeOfBytesToGetSize

    /**
     * Gets a XyoBridgeHashSet for this Block set when transferring to a bridge.
     *
     * @param hashProvider The hash creator to hash the blocks with.
     * @return A XyoBridgeHashSet for this block set.
     */
    fun getHashSet (hashProvider: XyoHash.XyoHashProvider) = GlobalScope.async {
        val hashes = ArrayList<XyoObject>()
        for (block in array) {
            if (block is XyoBoundWitness) {
                hashes.add(block.getHash(hashProvider).await())
            }
        }
        return@async XyoBridgeHashSet(hashes.toTypedArray())
    }

    companion object : XyoSingleTypeCreator() {
        override val major: Byte = 0x02
        override val minor: Byte = 0x09
        override val sizeOfBytesToGetSize: Int? = 4

        override fun readSize(byteArray: ByteArray): Int {
            return XyoUnsignedHelper.readUnsignedInt(byteArray)
        }

        override fun newInstance(majorType: Byte, minorType: Byte, array: Array<XyoObject>): XyoSingleTypeArrayBase {
            return XyoBridgeBlockSet(array)
        }
    }
}