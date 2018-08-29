package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha256

class XyoOriginChainStateManager {
    private var count = 0
    private var latestHash : XyoHash? = null

    val index : XyoIndex
        get() = XyoIndex(count)

    val previousHash : XyoPreviousHash?
        get() {
            if (latestHash != null) {
                XyoPreviousHash(latestHash!!)
            }
            return null

        }

    fun newOriginBlock (hash : XyoHash) {
        latestHash = hash
        count++
    }
}