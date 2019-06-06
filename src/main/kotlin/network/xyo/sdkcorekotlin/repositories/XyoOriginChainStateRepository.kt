package network.xyo.sdkcorekotlin.repositories

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

interface XyoOriginChainStateRepository {
    fun getIndex(): XyoObjectStructure?
    fun putIndex(index: XyoObjectStructure)
    fun getPreviousHash(): XyoObjectStructure?
    fun putPreviousHash(hash: XyoObjectStructure)
    fun getSigners(): Array<XyoSigner>
    fun removeOldestSigner()
    fun putSigner(signer: XyoSigner)
    fun getStatics(): Array<XyoObjectStructure>
    fun setStatics (statics: Array<XyoObjectStructure>)
    fun onBoundWitness ()
    fun getLastBoundWitnessTime () : Long?
    fun commit(): Deferred<Unit>
}