package network.xyo.sdkcorekotlin.origin

import kotlinx.coroutines.experimental.runBlocking
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoIndex
import network.xyo.sdkcorekotlin.data.heuristics.number.unsigned.XyoNumberUnsigned
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.hashing.XyoPreviousHash
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class XyoOriginVerify (boundWitnesses : ArrayList<XyoBoundWitness>, hashProvider: XyoHash.XyoHashProvider) {
    private val mapping = createMapping(boundWitnesses.toTypedArray(), hashProvider)
    private val chain = ArrayList<XyoOriginBlock>()

//    fun verify () : Boolean {
//        originBlocks.sortWith(sorter)
//        return true
//    }
//
//    private fun findFirsyBlock () : XyoOriginBlock {
//        val list = LinkedList<XyoOriginBlock>()
//        val keys = mapping.keys.toTypedArray()
//        val indexStartPoint = keys[0]
//    }

    private fun goDown (originBlock: XyoOriginBlock) : LinkedList<XyoOriginBlock> {
        val list = LinkedList<XyoOriginBlock>()
        var found = false
        var child : XyoOriginBlock? = originBlock

        while (!found) {
            if (child != null) {
                val subBlocks = getSubBlocks(child)

                if (subBlocks.size == 1) {
                    child = subBlocks[0]
                    list.addFirst(child)
                } else {
                    found = true
                }
            } else {
                found = true
            }
        }

        return list
    }


    private fun goUp (originBlock: XyoOriginBlock) : LinkedList<XyoOriginBlock> {
        val list = LinkedList<XyoOriginBlock>()
        var found = false
        var parent : XyoOriginBlock? = originBlock

        while (!found) {
            if (parent != null) {
                parent = getParentBlock(parent)

                if (parent != null) {
                    list.add(parent)
                }

            } else {
                found = true
            }
        }

        return list
    }

    private fun getParentBlock (originBlock: XyoOriginBlock) : XyoOriginBlock? {
        return mapping[XyoPreviousHash(originBlock.hash).typed.contentHashCode()]
    }

    private fun getSubBlocks (originBlock: XyoOriginBlock) : ArrayList<XyoOriginBlock> {
        val list = ArrayList<XyoOriginBlock>()

        for (hash in originBlock.hashes) {
            val hashValue = hash?.typed

            if (hashValue != null) {
                val subBlock = mapping[hashValue.contentHashCode()]

                if (subBlock != null) {
                    list.add(subBlock)
                }
            }
        }

        return list
    }

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

    inner class XyoOriginBlock (val boundWitness: XyoBoundWitness, hashProvider: XyoHash.XyoHashProvider) {
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
}