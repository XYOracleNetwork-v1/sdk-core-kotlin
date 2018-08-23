package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import java.security.*

abstract class XyoGeneralEc : XyoSigningObject() {
    val keyGenerator : KeyPairGenerator = KeyPairGenerator.getInstance("EC")
}


