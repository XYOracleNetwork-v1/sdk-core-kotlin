package network.xyo.sdkcorekotlin.node.testCatalogs

import network.xyo.sdkcorekotlin.network.XyoProcedureCatalog
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogFlags
import kotlin.experimental.and

object XyoBoundWitnessCatalog : XyoProcedureCatalog {
    override fun canDo(byteArray: ByteArray): Boolean {
        if (byteArray.isEmpty()) {
            return false
        }

        return byteArray[0].toInt() and XyoProcedureCatalogFlags.BOUND_WITNESS != 0
    }

    override fun choose(byteArray: ByteArray): ByteArray {
        return byteArrayOf(0x01)
    }

    override fun getEncodedCanDo(): ByteArray {
        return byteArrayOf(0x01)
    }
}