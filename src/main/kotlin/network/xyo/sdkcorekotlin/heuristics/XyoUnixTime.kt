package network.xyo.sdkcorekotlin.heuristics

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.nio.ByteBuffer

/**
 * A simple unix time heuristic
 */
class XyoUnixTime (byteArray: ByteArray) : XyoObjectStructure(byteArray, 0) {

    /**
     * The time when the heuristic was created.
     */
    val time : Long
        get() = ByteBuffer.wrap(valueCopy).long

    companion object : XyoInterpret {

        /**
         * A getter for the unix time, this can be added to a XYO Node, so that it can be included in future
         * Bound Witnesses.
         */
        val getter = object : XyoHeuristicGetter {
            override fun getHeuristic(): XyoObjectStructure? {
                val time = System.currentTimeMillis()
                val buffer = ByteBuffer.allocate(8).putLong(time)

                return XyoUnixTime(getObjectEncoded(XyoSchemas.UNIX_TIME, buffer.array()))
            }
        }

        override fun getInstance(byteArray: ByteArray): XyoUnixTime {
            return XyoUnixTime(byteArray)
        }
    }
}
