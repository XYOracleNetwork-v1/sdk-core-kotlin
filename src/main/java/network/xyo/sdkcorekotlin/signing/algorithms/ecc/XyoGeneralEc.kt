package network.xyo.sdkcorekotlin.signing.algorithms.ecc

import network.xyo.sdkcorekotlin.signing.XyoSigner
import java.security.*

abstract class XyoGeneralEc : XyoSigner() {
    val keyGenerator : KeyPairGenerator = KeyPairGenerator.getInstance("EC")
}


