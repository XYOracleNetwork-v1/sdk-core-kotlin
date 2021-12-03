package network.xyo.sdkcorekotlin.repositories

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
    fun getStaticHeuristics(): Array<XyoObjectStructure>
    fun setStaticHeuristics (statics: Array<XyoObjectStructure>)
    fun onBoundWitness ()
    fun getLastBoundWitnessTime () : Long?
    suspend fun commit()
}