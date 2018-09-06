package network.xyo.sdkcorekotlin.data.array

import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.XyoPayload
import network.xyo.sdkcorekotlin.data.array.multi.XyoMultiTypeArrayInt
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoTestPlaceholder

class XyoPayloadTest : XyoTestBase() {

    @kotlin.test.Test
    fun packAndUnpackPayload() {
        println("here")
        XyoRssi.enable()
        XyoTestPlaceholder.enable()
        val elementsInSignedPayload = arrayListOf(XyoTestPlaceholder())
        val elementsInUnsignedPayload = arrayOf<XyoObject>(XyoTestPlaceholder())
        val payload = XyoPayload(XyoMultiTypeArrayInt(elementsInSignedPayload.toTypedArray()), XyoMultiTypeArrayInt(elementsInUnsignedPayload))
        val packedPayload = payload.untyped.value!!
        val recreated = XyoPayload.createFromPacked(packedPayload).value!!
        println(bytesToString(recreated.untyped.value!!))
        assertXyoObject(payload, recreated)
    }
}