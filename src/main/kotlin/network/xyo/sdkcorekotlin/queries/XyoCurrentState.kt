package network.xyo.sdkcorekotlin.queries

import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

interface XyoCurrentState {
    /**
     * Gets the all of signers to use when creating the next origin block.
     *
     * @return all of the signers.
     */
    fun getSigners () : Array<XyoSigner>

    /**
     * The previous hash to be included in the next origin block.
     */
    val previousHash : XyoBuff?

    /**
     * The index of the origin chain.
     */
    val index : XyoBuff

    /**
     * The next public key to be used in the origin chain.
     */
    var nextPublicKey : XyoBuff?
}