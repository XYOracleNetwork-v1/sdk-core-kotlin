package network.xyo.sdkcorekotlin.crypto.signing

import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import java.security.PrivateKey

abstract class XyoPrivateKey : PrivateKey, XyoBuff()