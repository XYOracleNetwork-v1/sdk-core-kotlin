package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import java.nio.ByteBuffer

class XyoUnixTimeGetter : XyoHeuristicGetter {
    override fun getHeuristic(): XyoBuff? {
        val time = System.currentTimeMillis()
        val buffer = ByteBuffer.allocate(8).putLong(time)

        return XyoBuff.newInstance(XyoSchemas.UNIX_TIME, buffer.array())
    }
}