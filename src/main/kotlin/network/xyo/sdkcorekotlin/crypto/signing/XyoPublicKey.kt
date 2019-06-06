package network.xyo.sdkcorekotlin.crypto.signing

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.security.PublicKey

abstract class XyoPublicKey (byteArray: ByteArray, offset: Int) : PublicKey, XyoObjectStructure(byteArray, offset)