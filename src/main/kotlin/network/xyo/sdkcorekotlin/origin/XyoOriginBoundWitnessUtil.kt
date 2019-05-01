package network.xyo.sdkcorekotlin.origin

import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitnessUtil
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject

/**
 * An object for getting origin related items out of a bound witness. For example getting bridged blocks, indexes,
 * and other XYO reverent information.
 */
object XyoOriginBoundWitnessUtil {

    /**
     * Gets the bridged blocks from a bound witness.
     *
     * @param boundWitness The bound witness to get the bridge blocks from
     * @return Bridged blocks from the bound witness. Will return null if none are found.
     */
    fun getBridgedBlocks (boundWitness: XyoBoundWitness) : Iterator<XyoBuff>? {
        for (witness in boundWitness[XyoSchemas.WITNESS.id]) {
            if (witness is XyoIterableObject) {
                for (item in witness[XyoSchemas.BRIDGE_BLOCK_SET.id].iterator()) {
                    if (item is XyoIterableObject) {
                        return item.iterator
                    }
                }
            }
        }

        return null
    }

}