package network.xyo.sdkcorekotlin.repositories

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

interface XyoOriginChainStateRepository {
    fun getIndex(): XyoBuff?
    fun putIndex(index: XyoBuff)
    fun getPreviousHash(): XyoBuff?
    fun putPreviousHash(hash: XyoBuff)
    fun getSigners(): Array<XyoSigner>
    fun removeOldestSigner()
    fun putSigner(signer: XyoSigner)
    fun getStatics(): Array<XyoBuff>
    fun setStaticts (statics: Array<XyoBuff>)
    fun onBoundWitness ()
    fun getLastBoundWitnessTime () : Long?
    fun commit(): Deferred<Unit>
}