package network.xyo.sdkcorekotlin.signing

import network.xyo.sdkcorekotlin.data.XyoObject

/**
 * A base class for cryptographic signaturePacking.
 */
abstract class XyoSignature : XyoObject() {
    /**
     * The RAW encoded signature so that encodings do not get confused when verifying.
     */
    abstract val encodedSignature : ByteArray
}