package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.objects.XyoObjectCreator
import java.nio.ByteBuffer

class XyoUnixTimeGetter : XyoHeuristicGetter {

    @ExperimentalUnsignedTypes
    override fun getHeuristic(): ByteArray? {
        val time = System.currentTimeMillis()
        val buffer = ByteBuffer.allocate(8).putLong(time)

        return XyoObjectCreator.createObject(XyoSchemas.UNIX_TIME, buffer.array())
    }
}