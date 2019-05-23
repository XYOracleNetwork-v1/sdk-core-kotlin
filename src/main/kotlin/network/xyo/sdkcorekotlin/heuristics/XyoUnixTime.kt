package network.xyo.sdkcorekotlin.heuristics

import network.xyo.sdkcorekotlin.schemas.XyoInterpret
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import java.nio.ByteBuffer

/**
 * A simple unix time heuristic
 */
abstract class XyoUnixTime : XyoBuff() {

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
            override fun getHeuristic(): XyoBuff? {
                val time = System.currentTimeMillis()
                val buffer = ByteBuffer.allocate(8).putLong(time)

                return object : XyoUnixTime() {
                    override val allowedOffset: Int = 0
                    override var item: ByteArray = XyoBuff.getObjectEncoded(XyoSchemas.UNIX_TIME, buffer.array())
                }
            }
        }

        override fun getInstance(byteArray: ByteArray): XyoUnixTime {
            return object : XyoUnixTime() {
                override val allowedOffset: Int = 0
                override var item: ByteArray = byteArray
            }
        }
    }
}
