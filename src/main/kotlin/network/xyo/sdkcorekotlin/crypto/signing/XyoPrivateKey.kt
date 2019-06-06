package network.xyo.sdkcorekotlin.crypto.signing

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.security.PrivateKey

abstract class XyoPrivateKey(byteArray: ByteArray, offset: Int): PrivateKey, XyoObjectStructure(byteArray, offset)