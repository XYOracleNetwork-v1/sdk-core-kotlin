package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoTestPlaceholder
import org.junit.Test

class XyoPayloadTest : XyoTestBase() {

    @Test
    fun packAndUnpackPayload() {
        XyoRssi.enable()
        XyoTestPlaceholder.enable()
        val elementsInSignedPayload = arrayListOf(XyoTestPlaceholder())
        val elementsInUnsignedPayload = arrayOf<XyoObject>(XyoTestPlaceholder())
        val payload = XyoPayload(XyoMultiTypeArrayInt(elementsInSignedPayload.toTypedArray()), XyoMultiTypeArrayInt(elementsInUnsignedPayload))
        val packedPayload = payload.untyped
        val recreated = XyoPayload.createFromPacked(packedPayload)
        assertXyoObject(payload, recreated)
    }
}