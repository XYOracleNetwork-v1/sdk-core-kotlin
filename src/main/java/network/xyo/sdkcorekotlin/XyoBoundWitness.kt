package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.signing.XyoSigningObject

class XyoBoundWitness (private val numberOfParties : Int,
                       private val signers : Array<XyoSigningObject>,
                       private val payload : Array<XyoObject>) {

}