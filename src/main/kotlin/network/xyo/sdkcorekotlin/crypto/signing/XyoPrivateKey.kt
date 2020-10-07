package network.xyo.sdkcorekotlin.crypto.signing

import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure
import java.security.PrivateKey
/**
 * A base class for private key generation.
 * @param byteArray
 * @param offset
 */
@ExperimentalStdlibApi
abstract class XyoPrivateKey(byteArray: ByteArray? = null, offset: Int? = null): PrivateKey, XyoObjectStructure(byteArray, offset)