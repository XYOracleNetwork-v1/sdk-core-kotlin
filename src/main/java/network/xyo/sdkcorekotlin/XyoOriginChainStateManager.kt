package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import network.xyo.sdkcorekotlin.signing.XyoNextPublicKey
import network.xyo.sdkcorekotlin.signing.XyoSigningObject

class XyoOriginChainStateManager {
    private val currentSigners = ArrayList<XyoSigningObject>()
    private val waitingSigners = ArrayList<XyoSigningObject>()
    private var latestHash : XyoHash? = null

    var count = 0
    val allHashes = ArrayList<XyoHash>()
    val allPublicKeys = ArrayList<XyoObject>()
    var nextPublicKey : XyoNextPublicKey? = null

    val index : XyoIndex
        get() = XyoIndex(count)

    val previousHash : XyoPreviousHash?
        get() {
            val latestHashValue = latestHash
            if (latestHashValue != null) {
                return XyoPreviousHash(latestHashValue)
            }
            return null
        }

    fun getSigners () : Array<XyoSigningObject>{
        return currentSigners.toTypedArray()
    }

    fun addSigner (signer : XyoSigningObject) {
        val publicKeyValue = signer.publicKey.value ?: return
        if (signer.publicKey.error != null) return
        nextPublicKey = XyoNextPublicKey(publicKeyValue)
        waitingSigners.add(signer)
        allPublicKeys.add(publicKeyValue)
    }

    fun removeOldestSigner () {
        currentSigners.removeAt(0)
    }

    fun newOriginBlock (hash : XyoHash) {
        nextPublicKey = null
        allHashes.add(hash)
        latestHash = hash
        count++
        addWaitingSigner()
    }

    private fun addWaitingSigner () {
        if (waitingSigners.size > 0) {
            currentSigners.add(waitingSigners.first())
            waitingSigners.removeAt(0)
        }
    }
}