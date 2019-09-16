package network.xyo.sdkcorekotlin.repositories

import kotlinx.coroutines.Deferred
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkobjectmodelkotlin.structure.XyoObjectStructure

/**
 * An interface that is used to keep track of the state when creating the origin chain.
 */
 
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
    fun commit(): Deferred<Unit>
}