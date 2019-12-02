package network.xyo.sdkcorekotlin.crypto.signing

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.security.PrivateKey
/**
 * A base class for private key generation.
 * @param byteArray
 * @param offset
 */
abstract class XyoPrivateKey(byteArray: ByteArray, offset: Int): PrivateKey, XyoObjectStructure(byteArray, offset)