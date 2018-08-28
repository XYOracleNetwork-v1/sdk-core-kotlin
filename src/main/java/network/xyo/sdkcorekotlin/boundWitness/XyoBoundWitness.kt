package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.experimental.async
import network.xyo.sdkcorekotlin.XyoError
import network.xyo.sdkcorekotlin.XyoResult
import network.xyo.sdkcorekotlin.data.*
import network.xyo.sdkcorekotlin.data.array.multi.XyoKeySet
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayInt
import network.xyo.sdkcorekotlin.data.array.single.XyoSingleTypeArrayShort
import network.xyo.sdkcorekotlin.data.heuristics.number.signed.XyoRssi
import network.xyo.sdkcorekotlin.signing.XyoSignatureSet
import network.xyo.sdkcorekotlin.signing.XyoSigningObject
import java.nio.ByteBuffer

open class XyoBoundWitness (private val signers : Array<XyoSigningObject>,
                            private val payload : XyoPayload) : XyoObject() {

    val publicKeys = ArrayList<XyoKeySet>()
    val payloads = ArrayList<XyoPayload>()
    val signatures = ArrayList<XyoSignatureSet>()

    override val data: ByteArray
        get() = makeBoundWitness()

    override val id: ByteArray
        get() = byteArrayOf(major, minor)

    override val sizeIdentifierSize: Int?
        get() = 4

    private fun makeBoundWitness() : ByteArray {
        val setter = XyoByteArraySetter(3)
        setter.add(makePublicKeys().untyped, 0)
        setter.add(makePayloads().untyped, 1)
        setter.add(makeSignatures().untyped, 2)
        return setter.merge()
    }

    fun addKeySet(keySet: XyoKeySet) {
        publicKeys.add(keySet)
    }

    fun addPayload(payload : XyoPayload) {
        payloads.add(payload)
    }

    fun addSignatureSet(signatureSet : XyoSignatureSet) {
        signatures.add(signatureSet)
    }

    private fun makePublicKeys () : XyoSingleTypeArrayShort {
        return XyoSingleTypeArrayShort(XyoKeySet.major, XyoKeySet.minor, publicKeys.toTypedArray())
    }

    private fun makeSignatures () : XyoSingleTypeArrayShort {
        return XyoSingleTypeArrayShort(XyoSignatureSet.major, XyoSignatureSet.minor, signatures.toTypedArray())
    }

    private fun makePayloads () : XyoSingleTypeArrayInt {
        return XyoSingleTypeArrayInt(XyoPayload.major, XyoPayload.minor, payloads.toTypedArray())
    }

    private fun signCurrent (signer : XyoSigningObject) = async {
        val setter = XyoByteArraySetter(payloads.size + 1)
        setter.add(makePublicKeys().untyped, 0)
        for (i in 0..payloads.size - 1) {
            val payload = payloads[i] as? XyoPayload
            if (payload != null) {
                setter.add(payload.signedPayload.untyped, i + 1)
            } else {
                throw Exception()
            }
        }
        return@async signer.signData(setter.merge()).await()
    }

    private fun signBoundWitness () = async {
        val signatureSet = XyoSignatureSet(Array(signers.size, { i ->
            val signature = signCurrent(signers[i]).await()
            if (signature.error == null && signature.value != null) {
                signature.value!!
            } else {
                return@async XyoResult<XyoSignatureSet>(XyoError("Error: ${signature.error}, Value: ${signature.value}"))
            }
        }))
        return@async XyoResult(signatureSet)
    }

    fun signForSelf () = async {
        val signatureSet = signBoundWitness().await()
        if (signatureSet.error == null && signatureSet.value != null) {
            addSignatureSet(signatureSet.value!!)
            return@async true
        }

        return@async false
    }

    companion object : XyoObjectCreator() {
        override val major: Byte
            get() = 0x02

        override val minor: Byte
            get() = 0x01

        override val sizeOfBytesToGetSize: Int
            get() = 4

        override fun createFromPacked(byteArray: ByteArray): XyoObject {
            return XyoRssi(40)
        }

        override fun readSize(byteArray: ByteArray): Int {
            return ByteBuffer.wrap(byteArray).int
        }
    }
}