package network.xyo.sdkcorekotlin.node

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoUnixTime

class XyoUnixTimeGetter : XyoHeuristicGetter {
    override fun getHeuristic(): XyoObject? {
        return XyoUnixTime(System.currentTimeMillis())
    }
}