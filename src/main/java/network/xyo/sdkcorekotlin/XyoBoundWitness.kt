package network.xyo.sdkcorekotlin

import network.xyo.sdkcorekotlin.exceptions.ExceededNumberOfPartiesException
import network.xyo.sdkcorekotlin.exceptions.MissingPartyExeception
import network.xyo.sdkcorekotlin.signing.XyoSigningObject

class XyoBoundWitness (numberOfParties : Int, signers : Array<XyoSigningObject>) {
    private val mNumberOfParties = numberOfParties
    private val mSigners = signers
    private val mPublicKeys = ArrayList<Array<ByteArray>>()
    private var mPayloadsHash : ByteArray = hashDefault
    private val mPayloads = ArrayList<ByteArray>()
    private val mSignatures = ArrayList<Array<ByteArray>>()

    private fun addPublicKeys (publicKeys : Array<ByteArray>) {
        if (mPublicKeys.size < mNumberOfParties) {
            mPublicKeys.add(publicKeys)
        } else {
            throw ExceededNumberOfPartiesException("Public Keys", mNumberOfParties)
        }
    }

    private fun addSignatures (signatures : Array<ByteArray>) {
        if (mSignatures.size < mNumberOfParties) {
            mSignatures.add(signatures)
        } else {
            throw ExceededNumberOfPartiesException("Signatures", mNumberOfParties)
        }
    }

    private fun addPayload (payload : ByteArray) {
        if (mPayloads.size < mNumberOfParties) {
            mPayloads.add(payload)
        } else {
            throw ExceededNumberOfPartiesException("Payloads", mNumberOfParties)
        }
    }

    private fun makePayloadsHash () {
        if (mPayloads.size == mNumberOfParties && mPublicKeys.size == mNumberOfParties) {
            mPayloadsHash = hashDefault
        } else {
            throw MissingPartyExeception(mNumberOfParties, mPayloads.size)
        }
    }

    companion object {
        val hashDefault = byteArrayOf(0x00)
    }
}