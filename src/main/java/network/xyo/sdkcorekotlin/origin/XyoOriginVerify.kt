package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.XyoObject
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoNumberUnsigned
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash

class XyoOriginVerify (boundWitnesses : ArrayList<XyoBoundWitness>, hashProvider: XyoHash.XyoHashProvider) {
    private val mapping = createMapping(boundWitnesses.toTypedArray(), hashProvider)

//    private fun getEndpoint (boundWitness: XyoBoundWitness, party: Int) : XyoOriginBlock {
//
//    }

    private fun createMapping (boundWitnesses: Array<XyoBoundWitness>, hashProvider: XyoHash.XyoHashProvider) : HashMap<Int, XyoOriginBlock> {
        val map = HashMap<Int, XyoOriginBlock>()

        for (boundWitness in boundWitnesses) {
            val originBlock = XyoOriginBlock(boundWitness, hashProvider)
            map[originBlock.hash.typed.contentHashCode()] = originBlock

            for (hash in originBlock.hashes) {
                val hashValue = hash?.typed

                if (hashValue != null) {
                    map[hashValue.contentHashCode()] = originBlock
                }
            }
        }

        return map
    }

    inner open class XyoOriginBlock (val boundWitness: XyoBoundWitness, protected val hashProvider: XyoHash.XyoHashProvider) {
        val hash : XyoHash = getHashBlocking(hashProvider)

        val indexes : Array<Int?>
            get() {
                val dynamicIndexes = ArrayList<Int?>()
                for (payload in boundWitness.payloads) {
                    val index = payload.signedPayloadMapping[XyoIndex.id.contentHashCode()] as? XyoNumberUnsigned
                    dynamicIndexes.add(index?.number)
                }
                return dynamicIndexes.toTypedArray()
            }

        val hashes : Array<XyoPreviousHash?>
            get() {
                val dynamicIndexes = ArrayList<XyoPreviousHash?>()
                for (payload in boundWitness.payloads) {
                    val hash = payload.signedPayloadMapping[XyoPreviousHash.id.contentHashCode()] as? XyoPreviousHash
                    dynamicIndexes.add(hash)
                }
                return dynamicIndexes.toTypedArray()
            }

        private fun getHashBlocking (hashProvider: XyoHash.XyoHashProvider) : XyoHash {
            return runBlocking {
                return@runBlocking boundWitness.getHash(hashProvider).await()
            }
        }
    }

    inner class XyoPartyOriginBlock (boundWitness: XyoBoundWitness, hashProvider: XyoHash.XyoHashProvider, val party : Int) : XyoOriginBlock(boundWitness, hashProvider) {
        val index : Int?
            get() = (boundWitness.payloads[party].signedPayloadMapping[XyoIndex.id.contentHashCode()] as? XyoNumberUnsigned)?.number

        val previousHash : XyoPreviousHash?
            get() = boundWitness.payloads[party].signedPayloadMapping[XyoIndex.id.contentHashCode()] as? XyoPreviousHash

//        fun getPrevious () : XyoPartyOriginBlock? {
//            val originBlock = mapping[previousHash?.hash?.typed?.contentHashCode()]
//            return XyoPartyOriginBlock(originBlock!!.boundWitness, hashProvider, getPartyNumber(publicKey, boundWitness)!!)
//        }
//
//        fun getNext () : XyoPartyOriginBlock? {
//            val block = mapping[XyoPreviousHash(previousHash!!.hash).typed.contentHashCode()]
//            return XyoPartyOriginBlock(block.boundWitness, hashProvider, getPartyNumber(publicKey, boundWitness)!!)
//        }
    }

    private fun getPartyNumber (publicKey: XyoObject, boundWitness: XyoBoundWitness) : Int? {
        for (i in 0 until boundWitness.publicKeys.size)
            for (key in boundWitness.publicKeys[i].array) {
                if (key.typed.contentHashCode() == publicKey.typed.contentHashCode() ) {
                    return i
                }
            }

        return null
    }
}


//private fun goDown (originBlock: XyoPartyOriginBlock) : LinkedList<XyoPartyOriginBlock> {
//    val list = LinkedList<XyoPartyOriginBlock>()
//    var found = false
//    var child : XyoPartyOriginBlock? = originBlock
//
//    while (!found) {
//        if (child != null) {
//            val subBlocks = getSubBlocks(child)
//
//            if (subBlocks.size == 1) {
//                child = subBlocks[0]
//                list.addFirst(child)
//            } else {
//                found = true
//            }
//        } else {
//            found = true
//        }
//    }
//
//    return list
//}
//
//
//private fun goUp (originBlock: XyoPartyOriginBlock) : LinkedList<XyoPartyOriginBlock> {
//    val list = LinkedList<XyoPartyOriginBlock>()
//    var found = false
//    var parent : XyoPartyOriginBlock? = originBlock
//
//    while (!found) {
//        if (parent != null) {
//            parent = getParentBlock(parent)
//
//            if (parent != null) {
//                list.add(parent)
//            }
//
//        } else {
//            found = true
//        }
//    }
//
//    return list
//}
//
//private fun getSubBlocks (originBlock: XyoPartyOriginBlock) : ArrayList<XyoPartyOriginBlock> {
//    val list = ArrayList<XyoPartyOriginBlock>()
//
//    for (hash in originBlock.hashes) {
//        val hashValue = hash?.typed
//
//        if (hashValue != null) {
//            val subBlock = mapping[hashValue.contentHashCode()]
//
//            if (subBlock != null) {
//                list.add(subBlock)
//            }
//        }
//    }
//
//    return list
// }