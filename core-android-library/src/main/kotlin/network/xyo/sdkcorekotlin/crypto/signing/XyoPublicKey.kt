package network.xyo.sdkcorekotlin.crypto.signing

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.security.PublicKey

/**
 * A base class for public keys.
 * @param byteArray
 * @param offset
 */
abstract class XyoPublicKey (byteArray: ByteArray, offset: Int) : PublicKey, XyoObjectStructure(byteArray, offset)