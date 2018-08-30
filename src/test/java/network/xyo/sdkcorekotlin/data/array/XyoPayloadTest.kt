package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi

class XyoPayloadTest : XyoTestBase() {

    @kotlin.test.Test
    fun packAndUnpackPayload() {
        XyoRssi.enable()
        val elementsInSignedPayload = ArrayList<XyoObject>()
        val elementsInUnsignedPayload = arrayOf<XyoObject>(XyoRssi(-65))
        val payload = XyoPayload(XyoMultiTypeArrayInt(elementsInSignedPayload.toTypedArray()), XyoMultiTypeArrayInt(elementsInUnsignedPayload))
        val packedPayload = payload.untyped.value!!
        val recreated = XyoPayload.createFromPacked(packedPayload).value!!
        assertXyoObject(payload, recreated)
    }
}